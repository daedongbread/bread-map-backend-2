package com.depromeet.breadmapbackend.domain.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.user.User;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	void deleteByUser(User user);
}
