package com.depromeet.breadmapbackend.domain.admin.category.dto;

import javax.validation.constraints.NotBlank;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRequest {

	@NotBlank(message = "카테고리 이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private String categoryName;

	public CategoryRequest(String categoryName) {
		this.categoryName = categoryName;
	}
}
