package com.depromeet.breadmapbackend.domain.admin.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponse {

	private Long categoryId;
	private String categoryName;

	public CategoryResponse(Long categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
}
