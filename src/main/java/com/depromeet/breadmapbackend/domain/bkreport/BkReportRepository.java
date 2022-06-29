package com.depromeet.breadmapbackend.domain.bkreport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BkReportRepository extends JpaRepository<BkReport, Long> {
    List<BkReport> findAll();
}
