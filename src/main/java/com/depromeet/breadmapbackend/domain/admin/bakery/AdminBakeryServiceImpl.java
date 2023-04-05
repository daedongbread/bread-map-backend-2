package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImageRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewImageRepository;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.view.ReviewViewRepository;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.feign.client.SgisClient;
import com.depromeet.breadmapbackend.global.infra.feign.dto.SgisGeocodeDto;
import com.depromeet.breadmapbackend.global.infra.feign.dto.SgisTokenDto;
import com.depromeet.breadmapbackend.global.infra.feign.dto.SgisTranscoordDto;
import com.depromeet.breadmapbackend.global.infra.feign.exception.SgisFeignException;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomSGISKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBakeryServiceImpl implements AdminBakeryService {
    private final ProductRepository productRepository;
    private final BakeryRepository bakeryRepository;
    private final BakeryViewRepository bakeryViewRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryReportImageRepository bakeryReportImageRepository;
    private final ProductAddReportRepository productAddReportRepository;
    private final ProductAddReportImageRepository productAddReportImageRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewViewRepository reviewViewRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final S3Uploader s3Uploader;
    private final SgisClient sgisClient;
    private final CustomSGISKeyProperties customSGISKeyProperties;
    private final CustomAWSS3Properties customAWSS3Properties;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminSimpleBakeryDto> getBakeryList(@RequestParam int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        Page<Bakery> all = bakeryRepository.findPageAll(pageRequest);
        return PageResponseDto.of(all, AdminSimpleBakeryDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminBakeryDto getBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        List<AdminProductDto> productList = productRepository.findByBakery(bakery).stream()
                .filter(Product::isTrue)
                .map(AdminProductDto::new).collect(Collectors.toList());

        String image = (bakery.getImage().contains(customAWSS3Properties.getDefaultImage().getBakery())) ? null : bakery.getImage();
        return AdminBakeryDto.builder().bakery(bakery).image(image).productList(productList).build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        Page<Bakery> all = bakeryRepository.findByNameContainsOrderByUpdatedAt(name, pageRequest);
        return PageResponseDto.of(all, AdminSimpleBakeryDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryLocationDto getBakeryLatitudeLongitude(String address) {
        SgisTokenDto token = sgisClient.getToken(customSGISKeyProperties.getKey(), customSGISKeyProperties.getSecret());
        SgisGeocodeDto geocode = sgisClient.getGeocode(token.getResult().getAccessToken(), address);
        if (geocode.getResult() == null) throw new SgisFeignException();
        SgisTranscoordDto transcoord = sgisClient.getTranscoord(token.getResult().getAccessToken(),
                5179, 4326, geocode.getResult().getResultdata().get(0).getX(), geocode.getResult().getResultdata().get(0).getY());

        Double latitude = transcoord.getResult().getPosY();
        Double longitude = transcoord.getResult().getPosX();
        return BakeryLocationDto.builder().latitude(latitude).longitude(longitude).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBakery(BakeryAddRequest request) {
        Bakery bakery = Bakery.builder()
                .name(request.getName())
                .image((request.getImage() == null || request.getImage().isBlank()) ?
                        customAWSS3Properties.getDefaultImage().getBakery() + (new SecureRandom().nextInt(10) + 1) + ".png" :
                        request.getImage())
                .address(request.getAddress()).latitude(request.getLatitude()).longitude(request.getLongitude())
                .hours(request.getHours())
                .websiteURL(request.getWebsiteURL()).instagramURL(request.getInstagramURL()).facebookURL(request.getFacebookURL()).blogURL(request.getBlogURL())
                .phoneNumber(request.getPhoneNumber())
                .facilityInfoList(request.getFacilityInfoList())
                .status(request.getStatus())
                .build();
        bakeryRepository.save(bakery);
        bakeryViewRepository.save(BakeryView.builder().bakery(bakery).build());

        if (request.getProductList() != null && !request.getProductList().isEmpty()) { // TODO
            for (BakeryAddRequest.ProductAddRequest productAddRequest : request.getProductList()) {
                Product product = Product.builder()
                        .productType(productAddRequest.getProductType())
                        .name(productAddRequest.getProductName())
                        .price(productAddRequest.getPrice())
                        .image(productAddRequest.getImage())
                        .bakery(bakery).build(); // TODO
                productRepository.save(product);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBakery(Long bakeryId, BakeryUpdateRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        bakery.update(request.getName(),
                request.getAddress(), request.getLatitude(), request.getLongitude(), request.getHours(),
                request.getWebsiteURL(), request.getInstagramURL(), request.getFacebookURL(), request.getBlogURL(),
                request.getPhoneNumber(),
                (request.getImage() == null || request.getImage().isBlank()) ?
                        customAWSS3Properties.getDefaultImage().getBakery() + (new SecureRandom().nextInt(10) + 1) + ".png" :
                        request.getImage(),
                request.getFacilityInfoList(), request.getStatus());

        if (request.getProductList() != null && !request.getProductList().isEmpty()) { // TODO
            for (BakeryUpdateRequest.ProductUpdateRequest productUpdateRequest : request.getProductList()) {
                Product product;
                if (productUpdateRequest.getProductId() == null) { // 새로운 product 일 때
                    product = Product.builder()
                            .productType(productUpdateRequest.getProductType())
                            .name(productUpdateRequest.getProductName())
                            .price(productUpdateRequest.getPrice())
                            .image(productUpdateRequest.getImage())
                            .bakery(bakery).build(); // TODO
                    productRepository.save(product);
                } else { // 기존 product 일 때
                    product = productRepository.findById(productUpdateRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                    product.update(productUpdateRequest.getProductType(), productUpdateRequest.getProductName(),
                            productUpdateRequest.getPrice(), request.getImage());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long bakeryId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
        s3Uploader.deleteFileS3(product.getImage());
        reviewProductRatingRepository.deleteByProductId(productId);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)@Override
    public AdminImageBarDto getAdminImageBar(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return AdminImageBarDto.builder()
                .bakeryReportImageNum((int) bakeryReportImageRepository.countByBakery(bakery))
                .productAddReportImageNum((int) productAddReportImageRepository.countByBakery(bakery))
                .reviewImageNum((int) reviewImageRepository.countByBakery(bakery))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType type, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        PageRequest pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());

        PageResponseDto<AdminImageDto> adminImages;
        if (type.equals(AdminBakeryImageType.bakeryReportImage)) {
            Page<BakeryReportImage> bakeryReportImages = bakeryReportImageRepository.findPageByBakery(bakery, pageable);
            adminImages = PageResponseDto.of(bakeryReportImages, AdminImageDto::new);
            bakeryReportImages.forEach(BakeryReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.productAddReportImage)) {
            Page<ProductAddReportImage> productAddReportImages = productAddReportImageRepository.findPageByBakeryAndIsRegisteredIsTrue(bakery, pageable);
            adminImages = PageResponseDto.of(productAddReportImages, AdminImageDto::new);
            productAddReportImages.forEach(ProductAddReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.reviewImage)) {
            Page<ReviewImage> reviewImages = reviewImageRepository.findPageByBakeryAndIsHideIsFalse(bakery, pageable);
            adminImages = PageResponseDto.of(reviewImages, AdminImageDto::new);
            reviewImages.forEach(ReviewImage::unNew);
        } else throw new DaedongException(DaedongStatus.ADMIN_IMAGE_TYPE_EXCEPTION);
        return adminImages;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        if (type.equals(AdminBakeryImageType.bakeryReportImage)) {
            BakeryReportImage bakeryReportImage = bakeryReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_IMAGE_REPORT_NOT_FOUND));
            String replace = bakeryReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                bakeryReportImageRepository.delete(bakeryReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.productAddReportImage)) {
            ProductAddReportImage productAddReportImage = productAddReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND));

            String replace = productAddReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                productAddReportImageRepository.delete(productAddReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.reviewImage)) {
            ReviewImage reviewImage = reviewImageRepository.findByIdAndBakery(imageId, bakery)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_IMAGE_NOT_FOUND));

            String replace = reviewImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else reviewImage.hide();
        } else throw new DaedongException(DaedongStatus.ADMIN_IMAGE_TYPE_EXCEPTION);
    }

    private Boolean isUsedImage(Bakery bakery, String image) {
        Set<String> usedImage = bakery.getProductList().stream()
                .filter(product -> product.getImage() != null)
                .map(product -> {
                    String replace = product.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
                    return replace.split("/")[replace.split("/").length - 1];
                }).collect(Collectors.toSet());

        if (bakery.getImage() != null) {
            String replace = bakery.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            usedImage.add(replace.split("/")[replace.split("/").length - 1]);
        }
        return usedImage.contains(image);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<ProductAddReport> contents = productAddReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        return PageResponseDto.of(contents, ProductAddReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerProductAddImage(Long bakeryId, Long reportId, ProductAddImageRegisterRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        ProductAddReport productAddReport = productAddReportRepository.findByIdAndBakery(reportId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_NOT_FOUND));

        request.getImageIdList().forEach(
                id -> {
                    ProductAddReportImage productAddReportImage = productAddReportImageRepository.findByIdAndProductAddReport(id, productAddReport)
                            .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND));
                    productAddReportImage.register();
                }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProductAddReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        ProductAddReport productAddReport = productAddReportRepository.findByIdAndBakery(reportId, bakery)
                .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_NOT_FOUND));
        productAddReport.getImages().forEach(productAddReportImage -> {
            String replace = productAddReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            s3Uploader.deleteFileS3(replace);
        });
        productAddReportRepository.delete(productAddReport);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<BakeryUpdateReport> contents = bakeryUpdateReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        return PageResponseDto.of(contents, BakeryUpdateReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeBakeryUpdateReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        BakeryUpdateReport bakeryUpdateReport = bakeryUpdateReportRepository.findByIdAndBakery(reportId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryUpdateReport.change();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBakeryUpdateReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryUpdateReportRepository.deleteByIdAndBakery(reportId, bakery);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBakery(Long bakeryId) { // TODO : casacade
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        flagBakeryRepository.deleteByBakery(bakery);
        bakeryUpdateReportRepository.deleteByBakery(bakery);
        bakeryReportImageRepository.deleteByBakery(bakery);
        productAddReportRepository.deleteByBakery(bakery);
        reviewImageRepository.deleteByBakery(bakery);
        reviewProductRatingRepository.deleteByBakeryId(bakeryId);
        reviewRepository.findByBakery(bakery).forEach(review -> {
            reviewReportRepository.deleteByReview(review);
            reviewViewRepository.deleteByReview(review);
        });
        bakeryViewRepository.deleteByBakery(bakery);
        bakeryRepository.deleteById(bakeryId);
    }
}
