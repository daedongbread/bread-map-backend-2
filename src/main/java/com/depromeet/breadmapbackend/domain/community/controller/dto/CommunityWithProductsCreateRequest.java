package com.depromeet.breadmapbackend.domain.community.controller.dto;

import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CommunityCreateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
public record CommunityWithProductsCreateRequest(
    @NotNull @Size(min = 10, max = 500) String content,
    @Size(max = 10) List<String> images,
    @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
    CommunityType communityType,
    @Nullable
    Long bakeryId,
    @Valid List<ProductRatingRequest> productRatingList,
    @Valid List<NoExistProductRatingRequest> noExistProductRatingRequestList
) {

}
