package com.depromeet.breadmapbackend.domain.bakery;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;

public interface BakeryService {
	List<BakeryCardDto> getBakeryList(
		Long userId, BakerySortType sortBy, boolean filterBy, Double latitude,
		Double longitude, Double height, Double width
	);

	BakeryDto getBakery(Long userId, Long bakeryId);

	List<BakeryScoreBaseWithSelectedDate> getBakeriesScoreFactors();
}
