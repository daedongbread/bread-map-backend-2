package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure;

import java.util.Optional;

/**
 * RedisRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
public interface RedisRepository {
	Optional<Long> increment(String key);
}
