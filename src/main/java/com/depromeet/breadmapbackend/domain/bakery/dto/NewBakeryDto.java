package com.depromeet.breadmapbackend.domain.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

public record NewBakeryDto(
	Bakery bakery,
	boolean isFlagged,
	boolean isFollowed

) {
}
