package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlagRepository extends JpaRepository<Flag, Long> {
    Optional<Flag> findByUserAndName(User user, String name);
    Optional<Flag> findByUserAndId(User user, Long flagId);
    List<Flag> findByUser(User user);
    void deleteByUser(User user);
}
