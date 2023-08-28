package com.depromeet.breadmapbackend.domain.bakery.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByBakeryAndIsTrueIsTrue(Bakery bakery);

	List<Product> findByBakeryAndNameStartsWithAndIsTrueIsTrue(Bakery bakery, String name);

	Optional<Product> findByIdAndBakery(Long productId, Bakery bakery);

	Optional<Product> findByBakeryAndName(Bakery bakery, String name);

	@Query("select p from Product p join fetch p.bakery where p.id in :productIds")
	List<Product> findByIdIn(@Param("productIds") List<Long> productIds);

	@Query("select p from Product p where p.bakery.id = :bakeryId")
	List<Product> findByBakeryId(@Param("bakeryId") Long bakeryId);
}
