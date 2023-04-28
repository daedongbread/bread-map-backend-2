package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FlagBakeryRepository extends JpaRepository<FlagBakery, Long> {
    @Query("SELECT count(*) FROM FlagBakery fb INNER JOIN Flag f ON f = fb.flag WHERE fb.bakery = :bakery AND f.name = '가봤어요'")
    int countFlagNum(@Param("bakery") Bakery bakery);
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
