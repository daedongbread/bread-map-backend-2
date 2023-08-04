package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure;

/**
 * FlagBakeryExternalRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface FlagBakeryExternalRepository {

	Long countFlagBakeryByBakeryId(Long bakeryId);
}
