package com.depromeet.breadmapbackend.domain.admin.curation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.admin.curation.domain.Curation;

@Repository
public interface CurationRepository extends JpaRepository<Curation, Long> {

	List<Curation> findAllByActiveIsTrue();
}
