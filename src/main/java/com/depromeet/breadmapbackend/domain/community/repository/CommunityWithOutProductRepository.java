package com.depromeet.breadmapbackend.domain.community.repository;

import com.depromeet.breadmapbackend.domain.community.domain.extend.CommunityWithOutProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommunityWithOutProductRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
public interface CommunityWithOutProductRepository extends JpaRepository<CommunityWithOutProduct, Long> {

}
