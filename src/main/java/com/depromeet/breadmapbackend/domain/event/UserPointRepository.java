package com.depromeet.breadmapbackend.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
}
