package com.depromeet.breadmapbackend.domain.community.domain.dto;

import lombok.Getter;

@Getter
public class ProductRatingCommand {

    private final Long productId;
    private final Long rating;

    public ProductRatingCommand(Long productId, Long rating) {
        this.productId = productId;
        this.rating = rating;
    }
}
