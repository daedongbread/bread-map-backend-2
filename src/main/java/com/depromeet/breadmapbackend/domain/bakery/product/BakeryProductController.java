package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductDto;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.SimpleProductDto;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/bakeries")
@RequiredArgsConstructor
public class BakeryProductController {
    private final BakeryProductService bakeryProductService;
    @GetMapping("/{bakeryId}/products")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ProductDto>> getProductList(
            @PathVariable Long bakeryId,
            @RequestParam(required = false)
            @Size(max=20, message = "20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
            String name) {
        return new ApiResponse<>(bakeryProductService.getProductList(bakeryId, name));
    }

    @PostMapping("/{bakeryId}/product-add-reports")
    @ResponseStatus(HttpStatus.CREATED)
    public void productAddReport(
            @CurrentUser String oAuthId, @PathVariable Long bakeryId,
            @RequestBody @Validated(ValidationSequence.class) ProductReportRequest request) {
        bakeryProductService.productAddReport(oAuthId, bakeryId, request);
    }

//    @GetMapping("/{bakeryId}/products/search")
//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse<List<SimpleProductDto>> searchSimpleProductList(
//            @PathVariable Long bakeryId,
//            @RequestParam
//            @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
//            String name) {
//        return new ApiResponse<>(bakeryProductService.searchSimpleProductList(bakeryId, name));
//    }
}
