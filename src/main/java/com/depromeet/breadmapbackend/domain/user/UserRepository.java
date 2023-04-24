package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.oAuthInfo.oAuthId = :oAuthId")
    Optional<User> findByOAuthId(@Param("oAuthId") String oAuthId);
    @Query(value = "select u from User u", countQuery = "select count(u) from User u")
    Page<User> findPageAll(Pageable pageable);
//    Optional<User> findByNickName(String nickName);
    @Query("SELECT u FROM User u WHERE u.userInfo.nickName = :nickName")
    Optional<User> findByNickName(@Param("nickName") String nickName);
}
