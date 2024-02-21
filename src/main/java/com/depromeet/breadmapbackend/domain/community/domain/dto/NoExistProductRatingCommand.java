package com.depromeet.breadmapbackend.domain.community.domain.dto;

import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import lombok.Getter;

@Getter
public class NoExistProductRatingCommand {

    private final ProductType productType;
    private final String productName;
    private final String price;
    private final Long rating;

    public NoExistProductRatingCommand(ProductType productType, String productName, String price, Long rating) {
        this.productType = productType;
        this.productName = productName;
        this.price = price;
        this.rating = rating;
    }
}
