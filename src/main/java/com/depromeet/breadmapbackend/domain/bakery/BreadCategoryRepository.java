package com.depromeet.breadmapbackend.domain.bakery;


import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadCategoryRepository extends JpaRepository<BreadCategory, Long>, BreadCategoryRepositoryCustom {

}
