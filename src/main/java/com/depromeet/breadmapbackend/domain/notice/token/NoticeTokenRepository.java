package com.depromeet.breadmapbackend.domain.notice.token;

import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, Long> {
    void deleteByUser(User user);
    void deleteByDeviceToken(String deviceToken);
    Optional<NoticeToken> findByDeviceToken(String deviceToken);
    List<NoticeToken> findByUser(User user);
    Optional<NoticeToken> findByUserAndDeviceToken(User user, String deviceToken);
}
