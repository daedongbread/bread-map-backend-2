package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductDto;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.SimpleProductDto;

import java.util.List;

public interface BakeryProductService {
    List<ProductDto> findProductList(Long bakeryId);
    void productAddReport(String username, Long bakeryId, ProductReportRequest request);

    List<SimpleProductDto> searchSimpleProductList(Long bakeryId, String name);
}
