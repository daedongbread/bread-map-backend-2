package com.depromeet.breadmapbackend.domain.admin.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponse {

	private String categoryName;

	public CategoryResponse(String categoryName) {
		this.categoryName = categoryName;
	}
}
