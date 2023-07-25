package com.depromeet.breadmapbackend.domain.admin.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryAssembler;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryRequest;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryResponse;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository repository;

	@Transactional
	public Long addCategory(CategoryRequest request) {

		Category category = repository.save(CategoryAssembler.toEntity(request));

		return category.getId();
	}

	@Transactional
	public void updateCategory(Long categoryId, CategoryRequest request) {

		Category category = findCategoryById(categoryId);

		category.update(request);
	}

	private Category findCategoryById(Long categoryId) {
		return repository.findById(categoryId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND));
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		repository.deleteById(categoryId);
	}

	public List<CategoryResponse> findAllCategory() {

		List<Category> categories = repository.findAll();

		List<CategoryResponse> dtos = CategoryAssembler.toDto(categories);

		return dtos;
	}
}
