package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BakeryRepository extends JpaRepository<Bakery, Long>{
    List<Bakery> findTop20ByLatitudeBetweenAndLongitudeBetween(Double leftLatitude, Double rightLatitude, Double downLongitude, Double upLongitude);
    List<Bakery> findAll();
    Optional<Bakery> findById(Long bakeryId);
}
