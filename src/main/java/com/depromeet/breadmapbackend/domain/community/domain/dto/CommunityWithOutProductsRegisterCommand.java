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
public class CommunityWithOutProductsRegisterCommand extends BaseCommunityRegisterCommand {

    private final String title;

    public CommunityWithOutProductsRegisterCommand(
        String title,
        String content,
        List<String> images,
        CommunityType communityType,
        Long bakeryId
    ) {
        super(content, images, communityType, bakeryId);
        this.title = title;
    }
}
