package com.depromeet.breadmapbackend.domain.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.user.User;

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
			getPioneer(dto) != null ? getPioneer(dto).getId() : null,
			getPioneer(dto) != null ? getPioneer(dto).getNickName() : null,
			getPioneer(dto) != null ? getPioneer(dto).getUserInfo().getImage() : null,
			dto.bakery().getShortAddress(),
			getBakeryAddReport(dto) != null ? getBakeryAddReport(dto).getContent() : null,
			dto.isFlagged(),
			dto.isFollowed()
		);
	}

	private static BakeryAddReport getBakeryAddReport(final NewBakeryDto dto) {
		return dto.bakery().getBakeryAddReport();
	}

	private static User getPioneer(final NewBakeryDto dto) {
		return dto.bakery().getPioneer();
	}

}
