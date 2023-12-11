package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * HotKeywordRepositoru
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public interface HotKeywordRepository extends JpaRepository<HotKeyword, Long> {
	List<HotKeyword> findAllByOrderByRankingAsc();
}
