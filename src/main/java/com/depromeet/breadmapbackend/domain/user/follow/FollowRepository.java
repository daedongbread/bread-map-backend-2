package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
    List<Follow> findByToUser(User toUser);
    List<Follow> findByFromUser(User fromUser);
    Integer countByToUser(User toUser);
    Integer countByFromUser(User fromUser);
    void deleteByFromUser(User fromUser);
    void deleteByToUser(User toUser);
}
