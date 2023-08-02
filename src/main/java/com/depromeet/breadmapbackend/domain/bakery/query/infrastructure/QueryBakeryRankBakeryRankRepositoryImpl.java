package com.depromeet.breadmapbackend.domain.bakery.query.infrastructure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.QueryBakeryRankRepository;

import lombok.RequiredArgsConstructor;

/**
 * QueryBakeryRankRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
@Repository
@RequiredArgsConstructor
public class QueryBakeryRankBakeryRankRepositoryImpl implements QueryBakeryRankRepository {

	private final StringRedisTemplate redisTemplate;
	private final QueryBakeryRankJpaRepository queryBakeryRankJpaRepository;

	@Override
	public List<QueryBakeryRank> findByCalculatedDate(final LocalDate now, final Pageable pageable) {
		return null;
	}
}
