package com.depromeet.breadmapbackend.domain.search.dto;

import com.depromeet.breadmapbackend.global.dto.SQSDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBakeryMessage implements SQSDto {
	private Long bakeryId;
	private String bakeryName;
	// TODO
}
