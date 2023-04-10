package com.depromeet.breadmapbackend.domain.user.block;

import com.depromeet.breadmapbackend.domain.user.block.BlockUser;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {
    Optional<BlockUser> findByFromUserAndToUser(User fromUser, User toUser);
    List<BlockUser> findByFromUser(User fromUser);
}
