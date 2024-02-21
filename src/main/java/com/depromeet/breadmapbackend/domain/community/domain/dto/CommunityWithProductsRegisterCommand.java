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
public class CommunityWithProductsRegisterCommand extends BaseCommunityRegisterCommand {

    private final List<NoExistProductRatingCommand> noExistProductRatingCommandList;
    private final List<ProductRatingCommand> productRatingCommandList;

    public CommunityWithProductsRegisterCommand(
        String content,
        List<String> images,
        CommunityType communityType,
        Long bakeryId,
        List<NoExistProductRatingCommand> noExistProductRatingCommandList,
        List<ProductRatingCommand> productRatingCommandList
    ) {
        super(content, images, communityType, bakeryId);
        this.noExistProductRatingCommandList = noExistProductRatingCommandList;
        this.productRatingCommandList = productRatingCommandList;
    }
}
