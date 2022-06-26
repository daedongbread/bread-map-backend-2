package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
    List<Follow> findByToUser(User toUser);
    Integer countByToUser(User toUser);
    Integer countByFromUser(User fromUser);
}
