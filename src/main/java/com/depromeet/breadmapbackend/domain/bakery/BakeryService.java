package com.depromeet.breadmapbackend.domain.bakery;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryRankingCard;

public interface BakeryService {
	List<BakeryCardDto> getBakeryList(Long userId, BakerySortType sortBy, boolean filterBy, Double latitude,
		Double longitude, Double height, Double width);

	BakeryDto getBakery(String oAuthId, Long bakeryId);

	List<BakeryRankingCard> getBakeryRankingTop(final int size, final Long userId);
}
