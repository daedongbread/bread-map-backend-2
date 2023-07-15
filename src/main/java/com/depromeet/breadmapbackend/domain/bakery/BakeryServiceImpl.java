package com.depromeet.breadmapbackend.domain.bakery;

import static com.depromeet.breadmapbackend.domain.flag.FlagRepository.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBase;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.dto.CoordinateRange;
import com.depromeet.breadmapbackend.domain.bakery.sort.SortProcessor;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
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
	private final FlagRepository flagRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final ReviewService reviewService;
	private final List<SortProcessor> sortProcessors;
	private final BakeryViewEventStream bakeryViewEventStream;
	private final ReviewQueryRepository reviewQueryRepository;

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
		return bakeries.isEmpty() ?
			List.of() :
			getBakeryCardDtos(userId, sortBy, filterBy, latitude, longitude, bakeries);
	}

	@Transactional(rollbackFor = Exception.class)
	public BakeryDto getBakery(final Long userId, final Long bakeryId) {
		Bakery bakery = bakeryRepository.findByIdAndStatus(bakeryId, BakeryStatus.POSTING)
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

		final BakeryDto bakeryDto = BakeryDto.builder()
			.bakery(bakery)
			.flagCount(flagBakeryRepository.countFlagNum(bakery))
			.reviewList(reviewQueryRepository.findByUserIdAndBakery(userId, bakery))
			.userFlagBakery(flagBakeryRepository.findByBakeryAndUserId(bakery, userId).orElse(null))
			.build();

		bakeryViewEventStream.publish(createEventMessage(bakery));
		return bakeryDto;
	}

	@Override
	public List<BakeryScoreBaseWithSelectedDate> getBakeriesScoreFactors() {
		return bakeryQueryRepository.getBakeriesScoreFactors(LocalDate.now())
			.stream()
			.map(base -> new BakeryScoreBaseWithSelectedDate(base, LocalDate.now()))
			.toList();

	}

	private HashMap<String, String> createEventMessage(final Bakery bakery) {
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put("bakeryId", bakery.getId().toString());
		fieldMap.put("viewDate", LocalDate.now().toString());
		return fieldMap;
	}

	private List<BakeryCardDto> getBakeryCardDtos(
		final Long userId,
		final BakerySortType sortBy,
		final boolean filterBy,
		final Double latitude,
		final Double longitude,
		final List<Bakery> bakeries
	) {
		final Map<Long, List<Review>> reviewListForAllBakeries = reviewService.getReviewListInBakeries(userId, bakeries);
		final List<BakeryCountInFlag> bakeryCountInFlags = flagRepository.countFlagNum(bakeries);

		return bakeries
			.stream()
			.map(bakery -> BakeryCardDto.builder()
				.bakery(bakery)
				.flagNum(getFlagNum(bakeryCountInFlags, bakery))
				.reviewList(reviewListForAllBakeries.getOrDefault(bakery.getId(), List.of()))
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
			final Optional<Flag> flag = flagRepository.findByBakeryAndUserId(bakery, userId);
			return flag.isPresent() ? flag.get().getColor() : FlagColor.GRAY;
		}
	}

	private int getFlagNum(final List<BakeryCountInFlag> bakeryCountInFlags, final Bakery bakery) {
		return bakeryCountInFlags.stream()
			.filter(bakeryCountInFlag -> bakeryCountInFlag.getBakeryId().equals(bakery.getId()))
			.map(bakeryCountInFlag -> bakeryCountInFlag.getCount() == null ? 0L : bakeryCountInFlag.getCount())
			.findFirst()
			.orElse(0L).intValue();
	}
}
