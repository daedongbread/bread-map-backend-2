package com.depromeet.breadmapbackend.domain.notice.repository;

import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, Long> {
    void deleteByUser(User user);
}
