package com.depromeet.breadmapbackend.domain.bakery.dto;

public record NewBakeryCardDto(
	Long id,
	String image,
	String name,
	Long pioneerId,
	String pioneerNickname,
	String pioneerProfileImage,
	String shortAddress,
	String content,
	boolean isFlagged,
	boolean isFollowed
) {

	public NewBakeryCardDto(final NewBakeryDto dto) {
		this(
			dto.bakery().getId(),
			dto.bakery().getImage(),
			dto.bakery().getName(),
			dto.bakery().getPioneer() != null
				? dto.bakery().getPioneer().getId()
				: null,
			dto.bakery().getPioneer() != null
				? dto.bakery().getPioneer().getNickName()
				: null,
			dto.bakery().getPioneer() != null
				? dto.bakery().getPioneer().getUserInfo().getImage()
				: null,
			dto.bakery().getShortAddress(),
			dto.bakery().getBakeryAddReport() != null
				? dto.bakery().getBakeryAddReport().getContent()
				: null,
			dto.isFlagged(),
			dto.isFollowed()
		);
	}

}
