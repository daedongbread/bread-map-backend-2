package com.depromeet.breadmapbackend.domain.bakery.view;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BakeryViewRepository extends JpaRepository<BakeryView, Long> {
    Optional<BakeryView> findByBakery(Bakery bakery);
    void deleteByBakery(Bakery bakery);
}
