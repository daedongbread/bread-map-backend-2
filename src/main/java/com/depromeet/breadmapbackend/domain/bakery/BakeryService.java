package com.depromeet.breadmapbackend.domain.bakery;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.dto.NewBakeryCardDto;

public interface BakeryService {
	List<BakeryCardDto> getBakeryList(
		Long userId, BakerySortType sortBy, boolean filterBy, Double latitude,
		Double longitude, Double height, Double width
	);

	BakeryDto getBakery(Long userId, Long bakeryId);

	List<BakeryScoreBaseWithSelectedDate> getBakeriesScoreFactors(final LocalDate calculateDate);
	List<NewBakeryCardDto> getNewBakeryList(final Long userId);

}
