package com.depromeet.breadmapbackend.domain.admin.category.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryRequest;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryResponse;
import com.depromeet.breadmapbackend.domain.admin.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/category")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<Void> addCategory(
		@Valid @RequestBody CategoryRequest request
	) {
		categoryService.addCategory(request);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{categoryId}")
	public ResponseEntity<Void> updateCategory(
		@PathVariable Long categoryId,
		@Valid @RequestBody CategoryRequest request
	) {
		categoryService.updateCategory(categoryId, request);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {

		categoryService.deleteCategory(categoryId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/all")
	public ResponseEntity<List<CategoryResponse>> getAllCategory() {

		List<CategoryResponse> categories = categoryService.findAllCategory();

		return ResponseEntity.ok(categories);
	}
}
