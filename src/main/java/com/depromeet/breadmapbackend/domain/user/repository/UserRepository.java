package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query(value = "select * from user", countQuery = "select count(*) from user", nativeQuery = true)
    Page<User> findPageAll(Pageable pageable);
    Optional<User> findByNickName(String nickName);
    Optional<User> findByEmail(String email);
}
