package com.depromeet.breadmapbackend.domain.flag.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FlagBakeryRepository extends JpaRepository<FlagBakery, Long> {
    @Query("SELECT fb.bakery FROM FlagBakery fb where fb.flag = ?1")
    List<Bakery> findBakeryByFlag(Flag flag);
    List<FlagBakery> findByFlag(Flag flag);
    Optional<FlagBakery> findByBakeryAndFlagAndUser(Bakery bakery, Flag flag, User user);
    Optional<FlagBakery> findByBakeryAndUser(Bakery bakery, User user);
    @Query("SELECT fb.flag FROM FlagBakery fb where fb.bakery = ?1 and fb.user = ?2")
    Optional<Flag> findFlagByBakeryAndUser(Bakery bakery, User user);
    @Transactional
    void delete(FlagBakery flagBakery);
    void deleteByFlag(Flag flag);
    void deleteByBakery(Bakery bakery);
}
