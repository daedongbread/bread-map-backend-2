package com.depromeet.breadmapbackend.domain.bakery;

import static com.depromeet.breadmapbackend.domain.flag.FlagRepository.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.sort.SortProcessor;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryServiceImpl implements BakeryService {
	private final BakeryRepository bakeryRepository;
	private final BakeryQueryRepository bakeryQueryRepository;
	private final BakeryViewRepository bakeryViewRepository;
	private final UserRepository userRepository;
	private final FlagRepository flagRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final ReviewQueryRepository reviewQueryRepository;
	private final List<SortProcessor> sortProcessors;

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<BakeryCardDto> getBakeryList(
		final Long userId,
		final BakerySortType sortBy,
		final boolean filterBy,
		final Double latitude,
		final Double longitude,
		final Double latitudeDelta,
		final Double longitudeDelta
	) {
		final List<Bakery> bakeries =
			bakeryQueryRepository.findTop20BakeriesByCoordinateRange(
				CoordinateRange.of(latitude, latitudeDelta, longitude, longitudeDelta)
			);
		return bakeries.size() == 0 ?
			List.of(new BakeryCardDto()) :
			getBakeryCardDtos(userId, sortBy, filterBy, latitude, longitude, bakeries);
	}

	@Transactional(rollbackFor = Exception.class)
	public BakeryDto getBakery(String oAuthId, Long bakeryId) {
		User me = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Bakery bakery = bakeryRepository.findByIdAndStatus(bakeryId, BakeryStatus.POSTING)
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
		bakeryViewRepository.findByBakery(bakery)
			.orElseGet(() -> {
				BakeryView bakeryView = BakeryView.builder().bakery(bakery).build();
				return bakeryViewRepository.save(bakeryView);
			}).viewBakery();

		//        List<Review> reviewList = bakery.getReviewList().stream()
		//                .filter(Review::isValid)
		//                .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
		//                .collect(Collectors.toList());

		List<Review> reviewList = reviewQueryRepository.findReviewList(me, bakery);

		BakeryDto.BakeryInfo bakeryInfo = BakeryDto.BakeryInfo.builder()
			.bakery(bakery)
			.flagNum(flagBakeryRepository.countFlagNum(bakery))
			.rating(bakery.bakeryRating(reviewList))
			.reviewNum(reviewList.size())
			.build();
		BakeryDto.FlagInfo flagInfo = BakeryDto.FlagInfo.builder()
			.flagBakery(flagBakeryRepository.findByBakeryAndUser(bakery, me).orElse(null)).build();
		BakeryDto.PioneerInfo pioneerInfo = BakeryDto.PioneerInfo.builder().pioneer(bakery.getPioneer()).build();

		return BakeryDto.builder()
			.bakeryInfo(bakeryInfo)
			.flagInfo(flagInfo)
			.facilityInfoList(bakery.getFacilityInfoList())
			.pioneerInfo(pioneerInfo)
			.build();
	}

	@Override
	public List<BakeryRankingCard> getBakeryRankingTop(final int size, final Long userId) {
		return null;
	}

	private List<BakeryCardDto> getBakeryCardDtos(
		final Long userId,
		final BakerySortType sortBy,
		final boolean filterBy,
		final Double latitude,
		final Double longitude,
		final List<Bakery> bakeries
	) {
		final Map<Long, List<Review>> reviewListForAllBakeries =
			reviewQueryRepository.findReviewListInBakeries(userId, bakeries);
		final List<BakeryCountInFlag> bakeryCountInFlags = flagRepository.countFlagNum(bakeries);

		return bakeries
			.stream()
			.map(bakery -> BakeryCardDto.builder()
				.bakery(bakery)
				.flagNum(getFlagNum(bakeryCountInFlags, bakery))
				.reviewList(reviewListForAllBakeries.getOrDefault(bakery.getId(), null))
				.distance(bakery.getDistanceFromUser(latitude, longitude))
				.color(getFlagColor(filterBy, userId, bakery))
				.build())
			.sorted(getSortProcessor(sortBy).getComparator())
			.toList();
	}

	private SortProcessor getSortProcessor(final BakerySortType sortBy) {
		return sortProcessors.stream()
			.filter(sortProcessor -> sortProcessor.supports(sortBy))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION));
	}

	private FlagColor getFlagColor(final boolean filterBy, final Long userId, final Bakery bakery) {
		if (!filterBy)
			return FlagColor.ORANGE;
		else {
			final Optional<Flag> flag = flagBakeryRepository.findFlagByBakeryAndUser(bakery, userId);
			return flag.isPresent() ? flag.get().getColor() : FlagColor.GRAY;
		}
	}

	private int getFlagNum(final List<BakeryCountInFlag> bakeryCountInFlags, final Bakery bakery) {
		return bakeryCountInFlags.stream()
			.filter(bakeryCountInFlag -> bakeryCountInFlag.getBakeryId().equals(bakery.getId()))
			.map(BakeryCountInFlag::getCount)
			.findFirst()
			.orElse(0L).intValue();
	}
}
