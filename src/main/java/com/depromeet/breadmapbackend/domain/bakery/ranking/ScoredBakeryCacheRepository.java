package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

	public List<ScoredBakery> findScoredBakeryByCalculatedDate(final LocalDate calculatedDate, final int count) {

		final Optional<Set<String>> scoredBakeriesInString =
			Optional.ofNullable(redisTemplate.opsForZSet().reverseRange(getKey(calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), 0, count - 1));
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

	private String getKey(final String calculatedDate) {
		return "SCORED_BAKERY:" + calculatedDate;
	}

}
