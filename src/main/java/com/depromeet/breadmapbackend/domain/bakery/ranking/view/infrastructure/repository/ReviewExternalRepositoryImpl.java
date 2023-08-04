package com.depromeet.breadmapbackend.domain.bakery.ranking.view.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure.ReviewExternalRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRatingRepository;

import lombok.RequiredArgsConstructor;

/**
 * ReviewExternalRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Repository
@RequiredArgsConstructor
public class ReviewExternalRepositoryImpl implements ReviewExternalRepository {

	private final ReviewProductRatingRepository reviewProductRatingRepository;

	@Override
	public double findAvgRatingByBakeryId(final Long bakeryId) {
		return reviewProductRatingRepository.findAvgRatingByBakeryId(bakeryId);
	}
}
