package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductDto;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.SimpleProductDto;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BakeryProductServiceImpl implements BakeryProductService {
    private final BakeryRepository bakeryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductAddReportRepository productAddReportRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ProductDto> getProductList(Long bakeryId, String name) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        List<Product> products = (!StringUtils.hasText(name)) ?
                productRepository.findByBakeryAndIsTrueIsTrue(bakery) :
                productRepository.findByBakeryAndNameStartsWithAndIsTrueIsTrue(bakery, name);
        return products.stream()
                .map(ProductDto::new)
                .sorted(Comparator.comparing((ProductDto productDto) -> productDto.getProductType().getPriority())
                        .thenComparing(ProductDto::getRating, Comparator.reverseOrder())
                        .thenComparing(ProductDto::getName))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void productAddReport(String oAuthId, Long bakeryId, ProductReportRequest request) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        ProductAddReport productAddReport = ProductAddReport.builder()
                .bakery(bakery).user(user).name(request.getName()).price(request.getPrice()).build();
        productAddReportRepository.save(productAddReport);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (String image : request.getImages()) {
                ProductAddReportImage.builder().productAddReport(productAddReport).image(image).build();
            }
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleProductDto> searchSimpleProductList(Long bakeryId, String name) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return productRepository.findByBakeryAndNameStartsWithAndIsTrueIsTrue(bakery, name).stream()
                .map(SimpleProductDto::new)
                .sorted(Comparator.comparing((SimpleProductDto simpleProductDto) -> simpleProductDto.getProductType().getPriority())
                        .thenComparing(SimpleProductDto::getName)).collect(Collectors.toList());
    }
}
