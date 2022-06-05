package com.depromeet.breadmapbackend.domain.flag.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlagBakeryRepository extends JpaRepository<FlagBakery, Long> {
    Optional<FlagBakery> findByFlagIdAndBakeryId(Long flagId, Long bakeryId);
    Optional<FlagBakery> findByFlagAndBakery(Flag flag, Bakery bakery);
}
