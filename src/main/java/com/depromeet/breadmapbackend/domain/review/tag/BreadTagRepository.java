package com.depromeet.breadmapbackend.domain.review.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BreadTagRepository extends JpaRepository<BreadTag, Long> {
}
