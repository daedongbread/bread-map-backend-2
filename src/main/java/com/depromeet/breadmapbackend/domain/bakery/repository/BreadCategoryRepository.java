package com.depromeet.breadmapbackend.domain.bakery.repository;


import com.depromeet.breadmapbackend.domain.bakery.BreadCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadCategoryRepository extends JpaRepository<BreadCategory, Long>, BreadCategoryRepositoryCustom {

}
