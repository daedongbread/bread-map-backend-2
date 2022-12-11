package com.depromeet.breadmapbackend.domain.notice.repository;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUser(User user);
    Page<Notice> findTop20ByUserAndCreatedAtAfter(User user, LocalDateTime startTime, Pageable pageable);
    Page<Notice> findTop20ByUserAndCreatedAtBetween(User user, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<Notice> findTop20ByUserAndCreatedAtBefore(User user, LocalDateTime startTime, Pageable pageable);
    Page<Notice> findTop20ByUserAndCreatedAtBetweenOrderByCreatedAt(User user, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    void deleteByUser(User user);
}
