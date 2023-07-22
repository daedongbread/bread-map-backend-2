package com.depromeet.breadmapbackend.domain.admin.category.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;

public class CategoryAssembler {

	private CategoryAssembler() {
	}

	public static Category toEntity(CategoryRequest request) {
		return new Category(request.getCategoryName());
	}

	public static List<CategoryResponse> toDto(List<Category> categories) {
		return categories.stream().map(CategoryAssembler::toDto).collect(Collectors.toList());
	}

	public static CategoryResponse toDto(Category category) {
		return new CategoryResponse(category.getCategoryName());
	}
}
