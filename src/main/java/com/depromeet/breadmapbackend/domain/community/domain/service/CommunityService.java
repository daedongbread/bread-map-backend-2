package com.depromeet.breadmapbackend.domain.community.domain.service;

import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.dto.BaseCommunityRegisterCommand;

/**
 * CommunityService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
public interface CommunityService {

    boolean support(CommunityType communityType);

    void register(BaseCommunityRegisterCommand command, Long userId);
}
