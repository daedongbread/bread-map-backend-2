package com.depromeet.breadmapbackend.domain.admin.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
