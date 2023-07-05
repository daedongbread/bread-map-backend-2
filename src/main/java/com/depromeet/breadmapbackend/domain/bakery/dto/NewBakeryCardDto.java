package com.depromeet.breadmapbackend.domain.bakery.dto;

/**
 * NewBakeryDto
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/05
 */
public record NewBakeryCardDto(
	Long id,
	String image,
	String name,
	Long pioneerId,
	String shortAddress,
	String content,
	boolean isFlagged,
	boolean isFollowed
) {
}
