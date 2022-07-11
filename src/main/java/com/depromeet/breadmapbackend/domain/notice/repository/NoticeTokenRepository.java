package com.depromeet.breadmapbackend.domain.notice.repository;

import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, Long> {
}
