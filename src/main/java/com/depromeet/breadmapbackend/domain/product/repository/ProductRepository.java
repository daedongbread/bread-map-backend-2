package com.depromeet.breadmapbackend.domain.product.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBakery(Bakery bakery);
    List<Product> findByBakeryAndNameStartsWith(Bakery bakery, String name);
    Optional<Product> findByBakeryAndName(Bakery bakery, String name);
}
