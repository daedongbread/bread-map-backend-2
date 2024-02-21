package com.depromeet.breadmapbackend.domain.community.application;

import com.depromeet.breadmapbackend.domain.community.controller.Mapper;
import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithOutProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.controller.dto.CommunityWithProductsCreateRequest;
import com.depromeet.breadmapbackend.domain.community.domain.service.CommunityService;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CommunityFacade
 *
 * @author jaypark
 * @version 1.0.0
 * @since 1/12/24
 */

@Service
@RequiredArgsConstructor
public class CommunityFacade {

    private final List<CommunityService> communityService;

    public void registerCommunityWithOutProducts(
        final Long userId,
        final CommunityWithOutProductsCreateRequest request
    ) {
        communityService.stream()
            .filter(service -> service.support(request.communityType()))
            .findFirst()
            .orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_COMMUNITY_TYPE))
            .register(Mapper.of(request), userId);
    }

    public void registerCommunityWithProducts(
        final Long userId,
        final CommunityWithProductsCreateRequest request
    ) {
        communityService.stream()
            .filter(service -> service.support(request.communityType()))
            .findFirst()
            .orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_COMMUNITY_TYPE))
            .register(Mapper.of(request), userId);

    }
}
