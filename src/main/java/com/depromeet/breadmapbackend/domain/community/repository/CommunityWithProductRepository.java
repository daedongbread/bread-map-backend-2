package com.depromeet.breadmapbackend.domain.community.repository;

import com.depromeet.breadmapbackend.domain.community.domain.extend.CommunityWithProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommunityReviewRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
public interface CommunityWithProductRepository extends JpaRepository<CommunityWithProduct, Long> {

}
