package com.depromeet.breadmapbackend.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadDiaryEventRepository extends JpaRepository<BreadDiaryEvent, Long> {
}
