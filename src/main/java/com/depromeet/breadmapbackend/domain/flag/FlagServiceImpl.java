package com.depromeet.breadmapbackend.domain.flag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {
	private final FlagRepository flagRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final UserRepository userRepository;
	private final BakeryRepository bakeryRepository;
	private final ReviewRepository reviewRepository;

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<FlagDto> getFlags(Long userId) {
		return flagRepository.findByUserId(userId).stream()
			.map(flag -> FlagDto.builder()
				.flag(flag)
				.bakeryImageList(flag.getFlagBakeryList().stream()
					.filter(flagBakery -> flagBakery.getBakery().isPosting())
					.sorted(Comparator.comparing(FlagBakery::getCreatedAt)).limit(3)
					.map(flagBakery -> flagBakery.getBakery().getImage())
					.collect(Collectors.toList())).build())
			.collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public void addFlag(Long userId, FlagRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (flagRepository.findByUserAndName(user, request.getName()).isPresent())
			throw new DaedongException(DaedongStatus.FLAG_DUPLICATE_EXCEPTION);
		if (request.getColor().equals(FlagColor.GRAY))
			throw new DaedongException(DaedongStatus.FLAG_COLOR_EXCEPTION);

		flagRepository.save(
			Flag.builder()
				.user(user)
				.name(request.getName())
				.color(request.getColor())
				.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateFlag(Long userId, Long flagId, FlagRequest request) {
		Flag flag = flagRepository.findByUserIdAndId(userId, flagId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

		if (!flag.isEditable())
			throw new DaedongException(DaedongStatus.FLAG_UNEDIT_EXCEPTION);
		if (request.getColor().equals(FlagColor.GRAY))
			throw new DaedongException(DaedongStatus.FLAG_COLOR_EXCEPTION);

		flag.updateFlag(request.getName(), request.getColor());
	}

	@Transactional(rollbackFor = Exception.class)
	public void removeFlag(Long userId, Long flagId) {
		Flag flag = flagRepository.findByUserIdAndId(userId, flagId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

		if (!flag.isEditable())
			throw new DaedongException(DaedongStatus.FLAG_UNEDIT_EXCEPTION);

		flagRepository.delete(flag);
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public FlagBakeryDto getBakeryByFlag(Long userId, Long flagId) { // TODO page?

		final Flag flag = flagRepository.findByIdAndUserId(flagId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

		final List<Bakery> bakeryList = flagBakeryRepository.findByFlagAndUserIdOrderByCreatedAtDesc(flag, userId)
			.stream().map(FlagBakery::getBakery).toList();

		final List<FlagBakeryRepository.BakeryCountInFlag> bakeryCountInFlag =
			flagBakeryRepository.countFlagNum(bakeryList);

		final List<Review> reviews = reviewRepository.findByBakeryIn(bakeryList);

		final List<FlagBakeryDto.FlagBakeryInfo> flagBakeryInfoList =
			bakeryList.stream().map(bakery -> {
				final List<Review> filteredReview = reviews.stream()
					.filter(review -> review.getBakery().equals(bakery))
					.toList();

				return FlagBakeryDto.FlagBakeryInfo.builder()
					.bakery(bakery)
					.flagNum(getFlagCount(bakeryCountInFlag, bakery))
					.rating(getAverageRating(filteredReview))
					.reviewNum(filteredReview.size())
					.simpleReviewList(getSimpleReviewListFromReview(filteredReview))
					.build();
			}).toList();

		return FlagBakeryDto.builder()
			.flagBakeryInfoList(flagBakeryInfoList)
			.flag(flag)
			.build();
	}

	@Transactional(rollbackFor = Exception.class)
	public void addBakeryToFlag(Long userId, Long flagId, Long bakeryId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		Bakery bakery = bakeryRepository.findByIdAndStatus(bakeryId, BakeryStatus.POSTING)
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

		Flag flag = flagRepository.findById(flagId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

		flagBakeryRepository.findByBakeryAndUser(bakery, user)
			.ifPresent(flagBakery -> {
				if (flagBakery.getFlag().getId().equals(flagId))
					throw new DaedongException(DaedongStatus.FLAG_BAKERY_DUPLICATE_EXCEPTION);
				else
					flagBakeryRepository.delete(flagBakery);
			});

		flagBakeryRepository.save(
			FlagBakery.builder()
				.flag(flag)
				.bakery(bakery)
				.user(user)
				.build()
		);

	}

	@Transactional(rollbackFor = Exception.class)
	public void removeBakeryToFlag(Long userId, Long flagId, Long bakeryId) {
		flagBakeryRepository.delete(
			flagBakeryRepository.findByBakeryIdAndFlagIdAndUserId(bakeryId, flagId, userId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND))
		);
	}

	private List<MapSimpleReviewDto> getSimpleReviewListFromReview(final List<Review> filteredReview) {
		return filteredReview.stream()
			.sorted(Comparator.comparing(Review::getCreatedAt).reversed())
			.map(MapSimpleReviewDto::new)
			.limit(3)
			.toList();
	}

	private Integer getFlagCount(final List<FlagBakeryRepository.BakeryCountInFlag> bakeryCountInFlag,
		final Bakery bakery) {
		return bakeryCountInFlag.stream()
			.filter(flagCount -> flagCount.getBakeryId().equals(bakery.getId()))
			.findFirst()
			.map(bc -> bc.getCount().intValue())
			.orElse(0);
	}

	private double getAverageRating(final List<Review> filteredReview) {
		return filteredReview.stream()
			.map(Review::getAverageRating)
			.mapToDouble(Double::doubleValue)
			.average()
			.orElse(0)
			* 10 / 10.0;
	}
}
