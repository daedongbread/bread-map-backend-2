package com.depromeet.breadmapbackend.domain.subway;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayStationRepository extends JpaRepository<SubwayStation, Long> {
    List<SubwayStation> findByName(@Param("name") String name);
}
