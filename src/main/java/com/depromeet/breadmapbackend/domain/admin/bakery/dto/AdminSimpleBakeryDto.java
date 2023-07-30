package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import java.time.format.DateTimeFormatter;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminSimpleBakeryDto {
	private Long bakeryId;
	private String name;
	private String address;
	private String detailedAddress;
	private Integer bakeryReportImageNum;
	private Integer productAddReportNum;
	private Integer bakeryUpdateReportNum;
	private Integer newReviewNum;
	private String createdAt;
	private String modifiedAt;
	private BakeryStatus status;

	@Builder
	public AdminSimpleBakeryDto(
		Bakery bakery,
		Integer bakeryReportImageNum,
		Integer productAddReportNum,
		Integer bakeryUpdateReportNum,
		Integer newReviewNum
	) {
		this.bakeryId = bakery.getId();
		this.name = bakery.getName();
		this.address = bakery.getAddress();
		this.detailedAddress = bakery.getDetailedAddress();
		this.bakeryReportImageNum = bakeryReportImageNum;
		this.productAddReportNum = productAddReportNum;
		this.bakeryUpdateReportNum = bakeryUpdateReportNum;
		this.newReviewNum = newReviewNum;
		this.createdAt = bakery.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.modifiedAt = bakery.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.status = bakery.getStatus();
	}
}
