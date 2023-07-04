package com.depromeet.breadmapbackend.domain.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

/**
 * BakeryAddDto
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */
public record AddedBakeryCardDto(
	Long id,
	String image,
	String name,
	Long pioneerId,
	String shortAddress

) {
	public AddedBakeryCardDto(final Bakery bakery) {
		this(bakery.getId(),
			bakery.getImage(),
			bakery.getName(),
			bakery.getPioneer().getId(),
			bakery.getAddress()
		);
	}
}
