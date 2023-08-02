package com.depromeet.breadmapbackend.domain.bakery.query.infrastructure;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;
import com.depromeet.breadmapbackend.domain.bakery.query.domain.repository.QueryBakeryRankRepository;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;

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
public class QueryBakeryRankRepositoryImpl implements QueryBakeryRankRepository {
	private static final String BAKERY_RANK_KEY_PREFIX = "bakery_rank:";
	private final RedisTemplate<String, QueryBakeryRank> redisTemplate;
	private final QueryBakeryRankJpaRepository queryBakeryRankJpaRepository;

	@Override
	public List<QueryBakeryRank> findByCalculatedDate(final LocalDate calculatedDate, final Pageable pageable) {
		final String redisKey = getRedisKey(calculatedDate);
		final Set<QueryBakeryRank> bakeryRanks = redisTemplate.opsForZSet().range(redisKey, 0, pageable.getPageSize());

		return Optional.ofNullable(bakeryRanks)
			.stream()
			.flatMap(Collection::stream)
			.toList();
	}

	@Override
	public List<QueryBakeryRank> findByCalculatedDateFromDb(final LocalDate calculatedDate, final Pageable pageable) {
		return queryBakeryRankJpaRepository.findByCalculatedDate(calculatedDate, pageable)
			.stream()
			.map(QueryBakeryRankJpaEntity::toDomain)
			.toList();
	}

	private static String getRedisKey(final LocalDate calculatedDate) {
		return BAKERY_RANK_KEY_PREFIX + LocalDateParser.parse(calculatedDate);
	}
}
