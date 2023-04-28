package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBakeryAndIsTrueIsTrue(Bakery bakery);
    List<Product> findByBakeryAndNameStartsWithAndIsTrueIsTrue(Bakery bakery, String name);
    Optional<Product> findByIdAndBakery( Long productId, Bakery bakery);
    Optional<Product> findByBakeryAndName(Bakery bakery, String name);
}
