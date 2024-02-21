package com.depromeet.breadmapbackend.domain.community.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CommunityType
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */

@Getter
@AllArgsConstructor
public enum CommunityType {
    REVIEW(true),
    BREAD_PILGRIMAGE(false),
    BRAG_BREAD(false),
    BAKING(false),
    BREAD_TALK(false),
    ONE_DAY_CLASS(false),
    BREAD_GROUP_BUY(false),
    EVENT(false),
    ;

    private final boolean containsProduct;
}
