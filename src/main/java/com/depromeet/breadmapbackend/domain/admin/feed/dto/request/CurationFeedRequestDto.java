package com.depromeet.breadmapbackend.domain.admin.feed.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationFeedRequestDto {

	private Long bakeryId;

	private Long productId;

	private String reason;

	@Builder
	public CurationFeedRequestDto(Long bakeryId, Long productId, String reason) {
		this.bakeryId = bakeryId;
		this.productId = productId;
		this.reason = reason;
	}
}
