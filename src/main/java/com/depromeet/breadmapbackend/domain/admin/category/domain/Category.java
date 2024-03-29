package com.depromeet.breadmapbackend.domain.admin.category.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryRequest;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String categoryName;

	@Builder
	public Category(Long id, String categoryName) {
		this.id = id;
		this.categoryName = categoryName;
	}

	public Category update(CategoryRequest request) {
		return Category.builder().categoryName(request.getCategoryName()).build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Category category = (Category)o;
		return Objects.equals(id, category.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
