package com.depromeet.breadmapbackend.domain.admin.curation.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.curation.domain.Curation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationDto {

	private String curationType;
	private int like;
	private String thumbnailUrl;
	private String subTitle;
	private String description;
	private String redirectUrl;
	private List<CurationBakeryDto> curationBakeries;

	@Builder
	private CurationDto(String curationType, int like, String thumbnailUrl, String subTitle, String description,
		String redirectUrl, List<CurationBakeryDto> curationBakeries) {
		this.curationType = curationType;
		this.like = like;
		this.thumbnailUrl = thumbnailUrl;
		this.subTitle = subTitle;
		this.description = description;
		this.redirectUrl = redirectUrl;
		this.curationBakeries = curationBakeries;
	}

	public static CurationDto of(Curation curation) {
		return CurationDto.builder()
			.subTitle(curation.getSubTitle())
			.description(curation.getDescription())
			.redirectUrl(curation.getRedirectUrl())
			.thumbnailUrl(curation.getThumbnailUrl())
			.like(curation.getLike())
			.curationType(curation.getCurationType().toString())
			.curationBakeries()
			.build();
	}
}
