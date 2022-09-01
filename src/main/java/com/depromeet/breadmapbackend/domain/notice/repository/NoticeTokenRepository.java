package com.depromeet.breadmapbackend.domain.notice.repository;

import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, Long> {
    void deleteByUser(User user);
    Optional<NoticeToken> findByDeviceToken(String deviceToken);
    Optional<NoticeToken> findByUser(User user);
    Optional<NoticeToken> findByUserAndDeviceToken(User user, String deviceToken);
}
