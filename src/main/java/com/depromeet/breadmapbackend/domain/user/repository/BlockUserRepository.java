package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {
    Optional<BlockUser> findByUserAndBlockUser(User user, User blockUser);
    List<BlockUser> findByUser(User user);
}
