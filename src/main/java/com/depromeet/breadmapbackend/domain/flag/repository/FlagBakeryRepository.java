package com.depromeet.breadmapbackend.domain.flag.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FlagBakeryRepository extends JpaRepository<FlagBakery, Long> {
    Optional<FlagBakery> findByFlagIdAndBakeryId(Long flagId, Long bakeryId);
    Optional<FlagBakery> findByFlagAndBakery(Flag flag, Bakery bakery);
    @Query("SELECT fb.bakery FROM FlagBakery fb where fb.flag = ?1")
    List<Bakery> findBakeryByFlag(Flag flag);
    List<FlagBakery> findByFlag(Flag flag);
    @Transactional
    void delete(FlagBakery flagBakery);
    void deleteByFlag(Flag flag);
}
