package com.depromeet.breadmapbackend.domain.bakery.ranking;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository.FlagBakeryCount;

/**
 * ScoredBakeryServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScoredBakeryServiceImpl implements ScoredBakeryService {

	private static final int RANK_LIMIT = 40;

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final ScoredBakeryEventStream scoredBakeryEventStream;

	@Override
	@Transactional
	public int calculateBakeryScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		return scoredBakeryRepository.bulkInsert(
			rankTopScoredBakeries(bakeryScoreBaseList)
		);
		// TODO : rank view 변경 이벤트 발행 -> ( 랭킹뷰 재생성  기본적인 빵집 정보만 저장 )
		// TODO : 빵집 깃발 카운트 평정 출력해야하는데.....사용자 깃발 추가 여부, 평점 이벤트 발행해 말아??? 평점은... 안해도???
	}

	@Override
	public List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int size) {
		// TODO : rank view 조회 ( 빵집 데이터 + 나머지 필요 데이터는 entity 조회)
		final List<ScoredBakery> scoredBakeries = findScoredBakeryBy(LocalDate.now(), size);
		final List<FlagBakery> userFlaggedBakeries = findFlagBakeryBy(userId, scoredBakeries);
		final List<FlagBakeryCount> flagBakeryCounts = countFlagBakeryBy(scoredBakeries);

		return scoredBakeries.stream()
			.map(bakeryScores -> from(userFlaggedBakeries, bakeryScores, flagBakeryCounts))
			.limit(size)
			.toList();
	}

	private List<ScoredBakery> rankTopScoredBakeries(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		final List<ScoredBakery> sortedBakeryRank = sortBakeriesByScore(bakeryScoreBaseList);
		final int rankLimit = Math.min(RANK_LIMIT, sortedBakeryRank.size());
		final List<ScoredBakery> scoredBakeries = sortedBakeryRank.subList(0, rankLimit);

		int rank = 1;
		for (final ScoredBakery scoredBakery : scoredBakeries) {
			scoredBakery.setRank(rank++);
		}
		return scoredBakeries;
	}

	private List<ScoredBakery> sortBakeriesByScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		return bakeryScoreBaseList
			.stream()
			.map(ScoredBakery::from)
			.sorted(
				Comparator.comparing(ScoredBakery::getTotalScore)
					.thenComparing(scoredBakery -> scoredBakery.getBakery().getId()).reversed()
			)
			.toList();
	}

	private List<ScoredBakery> findScoredBakeryBy(final LocalDate calculatedDate, final int size) {
		log.info("findScoredBakeryBy calculatedDate: {}, size: {}", calculatedDate, size);
		final List<ScoredBakery> ranksFromDb = getRanksFromDb(calculatedDate, size);
		log.info("findScoredBakeryBy ranksFromDb: {}", ranksFromDb.size());
		if (!ranksFromDb.isEmpty()) {
			return ranksFromDb;
		}

		scoredBakeryEventStream.publishCalculateRankingEvent(calculatedDate);

		final List<ScoredBakery> lastCalculatedRank = getLastCalculatedRanks(calculatedDate, size);
		log.info("findScoredBakeryBy getLastCalculatedRanks: {}", lastCalculatedRank.size());
		if (!lastCalculatedRank.isEmpty()) {
			return lastCalculatedRank;
		}

		throw new DaedongException(DaedongStatus.CALCULATING_BAKERY_RANKING);
	}

	private List<ScoredBakery> getLastCalculatedRanks(final LocalDate calculatedDate, final int size) {
		return scoredBakeryRepository.findScoredBakeryByCalculatedDate(calculatedDate.minusDays(1L), size);
	}

	private List<ScoredBakery> getRanksFromDb(final LocalDate calculatedDate, final int size) {
		return scoredBakeryRepository.findScoredBakeryByCalculatedDate(calculatedDate, size);
	}

	private List<FlagBakery> findFlagBakeryBy(final Long userId, final List<ScoredBakery> bakeriesScores) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(
			userId,
			bakeriesScores.stream()
				.map(scoredBakery -> scoredBakery.getBakery().getId())
				.toList()
		);
	}

	private List<FlagBakeryCount> countFlagBakeryBy(final List<ScoredBakery> bakeriesScores) {
		return flagBakeryRepository.countFlagBakeryByBakeryIdIn(
			bakeriesScores.stream()
				.map(scoredBakery -> scoredBakery.getBakery().getId())
				.toList()
		);
	}

	private BakeryRankingCard from(
		final List<FlagBakery> flagBakeryList,
		final ScoredBakery bakeryScores,
		final List<FlagBakeryCount> flagBakeryCounts
	) {
		return BakeryRankingCard.builder()
			.id(bakeryScores.getBakery().getId())
			.flagNum(getFlagCount(bakeryScores, flagBakeryCounts))
			.name(bakeryScores.getBakery().getName())
			.image(bakeryScores.getBakery().getImages().isEmpty()
					? ""
					: bakeryScores.getBakery().getImages().get(0).describeConstable().orElse(""))
			.shortAddress(bakeryScores.getBakery().getShortAddress())
			.isFlagged(isUserFlaggedBakery(bakeryScores, flagBakeryList))
			.calculatedDate(bakeryScores.getCalculatedDate())
			.build();
	}

	private static Long getFlagCount(final ScoredBakery bakeryScores, final List<FlagBakeryCount> flagBakeryCounts) {
		return flagBakeryCounts.stream()
			.filter(f -> f.getBakeryId().equals(bakeryScores.getBakery().getId()))
			.findFirst()
			.map(FlagBakeryCount::getCount).orElse(0L);
	}

	private boolean isUserFlaggedBakery(
		final ScoredBakery bakeryScores,
		final List<FlagBakery> flagBakeryList
	) {
		return flagBakeryList.stream()
			.anyMatch(flagBakery ->
				flagBakery.getBakery().getId()
					.equals(bakeryScores.getBakery().getId())
			);
	}
}
