package com.depromeet.breadmapbackend.domain.community.domain.dto;

import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import java.util.List;
import lombok.Getter;

/**
 * CommunityRegisterCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/21/23
 */
@Getter
public abstract class BaseCommunityRegisterCommand {

    private final String content;
    private final List<String> images;
    private final CommunityType communityType;
    private final Long bakeryId;

    protected BaseCommunityRegisterCommand(
        String content,
        List<String> images,
        CommunityType communityType,
        Long bakeryId
    ) {
        this.content = content;
        this.images = images;
        this.communityType = communityType;
        this.bakeryId = bakeryId;
    }
}
