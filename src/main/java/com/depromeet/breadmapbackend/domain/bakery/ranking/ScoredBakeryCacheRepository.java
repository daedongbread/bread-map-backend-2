package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryCacheRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@RequiredArgsConstructor
@Repository
public class ScoredBakeryCacheRepository {

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public List<ScoredBakery> findBakeriesRankTop(final int count) {

		final Optional<Set<String>> scoredBakeriesInString = Optional.ofNullable(
			redisTemplate.opsForZSet().range(getKey(), 0, count - 1));
		return scoredBakeriesInString
			.map(this::getScoredBakeryListFrom)
			.orElseGet(List::of);
	}

	private List<ScoredBakery> getScoredBakeryListFrom(final Set<String> scoredBakery) {
		return scoredBakery.stream()
			.map(jsonString -> {
				try {
					return objectMapper.readValue(jsonString, ScoredBakery.class);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}).toList();
	}

	private String getKey() {
		// final LocalDate now = LocalDate.now();
		// WeekFields weekFields = WeekFields.of(Locale.getDefault());
		// int weekOfMonth = now.get(weekFields.weekOfMonth());
		return "SCORED_BAKERY"; // + weekOfMonth;
	}

}
