package com.depromeet.breadmapbackend.domain.admin.category.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryRequest;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryResponse;
import com.depromeet.breadmapbackend.domain.admin.category.service.CategoryService;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/category")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addCategory(
		@Valid @RequestBody CategoryRequest request
	) {
		categoryService.addCategory(request);
	}

	@PatchMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateCategory(
		@PathVariable Long categoryId,
		@Valid @RequestBody CategoryRequest request
	) {
		categoryService.updateCategory(categoryId, request);
	}

	@DeleteMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long categoryId) {

		categoryService.deleteCategory(categoryId);
	}

	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<CategoryResponse>> getAllCategory() {

		List<CategoryResponse> categories = categoryService.findAllCategory();

		return new ApiResponse<>(categories);
	}
}
