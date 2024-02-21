package com.depromeet.breadmapbackend.domain.community.domain.service;

import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.dto.BaseCommunityRegisterCommand;
import com.depromeet.breadmapbackend.domain.community.repository.CommunityWithProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CommunityReviewService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/21/23
 */
@Service
@RequiredArgsConstructor
public class CommunityWithProductService implements CommunityService {

    private final CommunityWithProductRepository communityWithProductRepository;

    @Override
    public boolean support(final CommunityType communityType) {
        return communityType == CommunityType.REVIEW;
    }

    @Override
    public void register(BaseCommunityRegisterCommand command, Long userId) {

    }

}
