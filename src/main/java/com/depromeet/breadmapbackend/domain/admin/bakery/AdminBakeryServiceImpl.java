package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryImageType;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryQueryRepository;
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
import com.depromeet.breadmapbackend.domain.notice.NoticeService;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.view.ReviewViewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowEvent;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.feign.sgis.client.SgisClient;
import com.depromeet.breadmapbackend.global.infra.feign.sgis.dto.SgisGeocodeDto;
import com.depromeet.breadmapbackend.global.infra.feign.sgis.dto.SgisTokenDto;
import com.depromeet.breadmapbackend.global.infra.feign.sgis.dto.SgisTranscoordDto;
import com.depromeet.breadmapbackend.global.infra.feign.exception.FeignException;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomSGISKeyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBakeryServiceImpl implements AdminBakeryService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BakeryRepository bakeryRepository;
    private final BakeryQueryRepository bakeryQueryRepository;
    private final BakeryViewRepository bakeryViewRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryReportImageRepository bakeryReportImageRepository;
    private final ProductAddReportRepository productAddReportRepository;
    private final ProductAddReportImageRepository productAddReportImageRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final S3Uploader s3Uploader;
    private final SgisClient sgisClient;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomSGISKeyProperties customSGISKeyProperties;
    private final CustomAWSS3Properties customAWSS3Properties;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminBakeryAlarmBar getBakeryAlarmBar() {
        Integer bakeryReportImageNum = bakeryReportImageRepository.countByIsNewIsTrue();
        Integer productAddReportNum = productAddReportRepository.countByIsNewIsTrue();
        Integer bakeryUpdateReportNum = bakeryUpdateReportRepository.countByIsNewIsTrue();
        Integer newReviewNum = reviewRepository.countByIsNewIsTrue();

        return AdminBakeryAlarmBar.builder()
                .bakeryReportImageNum(bakeryReportImageNum)
                .productAddReportNum(productAddReportNum)
                .bakeryUpdateReportNum(bakeryUpdateReportNum)
                .newReviewNum(newReviewNum)
                .build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminSimpleBakeryDto> getBakeryList(List<AdminBakeryFilter> filterBy, String name, int page) {
        Page<Bakery> bakeries = bakeryQueryRepository.getAdminBakeryList(filterBy, name, page);
        List<AdminSimpleBakeryDto> contents = bakeries.getContent().stream()
                .map(bakery -> AdminSimpleBakeryDto.builder()
                        .bakery(bakery)
                        .bakeryReportImageNum(bakeryReportImageRepository.countByBakeryAndIsNewIsTrue(bakery))
                        .productAddReportNum(productAddReportRepository.countByBakeryAndIsNewIsTrue(bakery))
                        .bakeryUpdateReportNum(bakeryUpdateReportRepository.countByBakeryAndIsNewIsTrue(bakery))
                        .newReviewNum(reviewRepository.countByBakeryAndIsNewIsTrue(bakery)).build())
                .collect(Collectors.toList());
        return PageResponseDto.of(bakeries, contents);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminBakeryDto getBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        List<AdminProductDto> productList = productRepository.findByBakeryAndIsTrueIsTrue(bakery).stream()
                .map(AdminProductDto::new).collect(Collectors.toList());

        String image = (bakery.getImage().contains(customAWSS3Properties.getDefaultImage().getBakery())) ? null : bakery.getImage();
        return AdminBakeryDto.builder().bakery(bakery).pioneer(bakery.getPioneer()).image(image).productList(productList).build();
    }

//    @Transactional(readOnly = true, rollbackFor = Exception.class)
//    public PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page) {
//        PageRequest pageRequest = PageRequest.of(page, 20);
//        Page<Bakery> all = bakeryRepository.findByNameContainsOrderByUpdatedAt(name, pageRequest);
//        return PageResponseDto.of(all, AdminSimpleBakeryDto::new);
//    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryLocationDto getBakeryLatitudeLongitude(String address) {
        SgisTokenDto token = sgisClient.getToken(customSGISKeyProperties.getKey(), customSGISKeyProperties.getSecret());
        SgisGeocodeDto geocode = sgisClient.getGeocode(token.getResult().getAccessToken(), address);
        if (geocode.getResult() == null) throw new FeignException();
        SgisTranscoordDto transcoord = sgisClient.getTranscoord(token.getResult().getAccessToken(),
                5179, 4326, geocode.getResult().getResultdata().get(0).getX(), geocode.getResult().getResultdata().get(0).getY());

        Double latitude = transcoord.getResult().getPosY();
        Double longitude = transcoord.getResult().getPosX();
        return BakeryLocationDto.builder().latitude(latitude).longitude(longitude).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public BakeryAddDto addBakery(BakeryAddRequest request) {
        User pioneer = (request.getPioneerId() == null) ? null : userRepository.findById(request.getPioneerId()).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = Bakery.builder()
                .name(request.getName())
                .image((StringUtils.hasText(request.getImage())) ? request.getImage() :
                        customAWSS3Properties.getCloudFront() + "/" +
                        customAWSS3Properties.getDefaultImage().getBakery() + (new SecureRandom().nextInt(10) + 1) + ".png")
                .address(request.getAddress()).latitude(request.getLatitude()).longitude(request.getLongitude())
                .hours(request.getHours())
                .websiteURL(request.getWebsiteURL()).instagramURL(request.getInstagramURL()).facebookURL(request.getFacebookURL()).blogURL(request.getBlogURL())
                .phoneNumber(request.getPhoneNumber())
                .facilityInfoList(request.getFacilityInfoList())
                .status(request.getStatus())
                .pioneer(pioneer)
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
        if (pioneer != null)
            eventPublisher.publishEvent(BakeryAddEvent.builder().userId(pioneer.getId()).bakeryId(bakery.getId()).bakeryName(bakery.getName()).build());
        return BakeryAddDto.builder().bakeryId(bakery.getId()).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBakery(Long bakeryId, BakeryUpdateRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        bakery.update(request.getName(),
                request.getAddress(), request.getLatitude(), request.getLongitude(), request.getHours(),
                request.getWebsiteURL(), request.getInstagramURL(), request.getFacebookURL(), request.getBlogURL(),
                request.getPhoneNumber(),
                (StringUtils.hasText(request.getImage())) ? request.getImage() :
                        customAWSS3Properties.getCloudFront() + "/" +
                        customAWSS3Properties.getDefaultImage().getBakery() + (new SecureRandom().nextInt(10) + 1) + ".png",
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
                            productUpdateRequest.getPrice(), productUpdateRequest.getImage());
                }
            }
        }

        updatePioneer(bakery, request.getPioneerId());
    }

    private void updatePioneer(Bakery bakery, Long pioneerId) {
        User beforePioneer = bakery.getPioneer();
        User pioneer = (pioneerId == null) ? null : userRepository.findById(pioneerId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        // 1. 그전에 null 이번에도 null -> 아무것도 안함
        // 2. 그전에 null 이번에는 있음 -> 업데이트와 알림
        // 3. 그전에 있음 이번에는 null -> 업데이트
        // 4. 그전에 있음 이번에도 있는데 다름 -> 업데이트와 알림
        // 4. 그전에 있음 이번에도 있는데 같음 -> 아무것도 안함
        if (pioneer == null && beforePioneer != null) {
            bakery.updatePioneer(pioneer);
        }
        else if (pioneer != null && !pioneer.equals(beforePioneer)) {
            bakery.updatePioneer(pioneer);
            eventPublisher.publishEvent(BakeryAddEvent.builder().userId(pioneer.getId()).bakeryId(bakery.getId()).bakeryName(bakery.getName()).build());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long bakeryId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
        s3Uploader.deleteFileS3(product.getImage());
        reviewProductRatingRepository.deleteByProductId(productId);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminBakeryIsNewDto getAdminBakeryIsNewBar(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return AdminBakeryIsNewDto.builder()
                .adminImageIsNew(bakeryReportImageRepository.existsByBakeryAndIsNewIsTrue(bakery) ||
                        productAddReportImageRepository.existsByBakeryAndIsNewIsTrue(bakery) ||
                        reviewImageRepository.existsByBakeryAndIsNewIsTrue(bakery))
                .productAddReportIsNew(productAddReportRepository.existsByBakeryAndIsNewIsTrue(bakery))
                .bakeryUpdateReportIsNew(bakeryUpdateReportRepository.existsByBakeryAndIsNewIsTrue(bakery))
                .newReviewIsNew(reviewRepository.existsByBakeryAndIsNewIsTrue(bakery))
                .build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminImageBarDto getAdminImageBar(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return AdminImageBarDto.builder()
                .bakeryReportImageNum(bakeryReportImageRepository.countByBakery(bakery))
                .productAddReportImageNum(productAddReportImageRepository.countByBakeryAndIsRegisteredIsTrue(bakery))
                .reviewImageNum(reviewImageRepository.countByBakeryAndIsRegisteredIsTrue(bakery))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType type, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        PageRequest pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());

        PageResponseDto<AdminImageDto> adminImages;
        if (type.equals(AdminBakeryImageType.BAKERY_REPORT_IMAGE)) {
            Page<BakeryReportImage> bakeryReportImages = bakeryReportImageRepository.findPageByBakery(bakery, pageable);
            adminImages = PageResponseDto.of(bakeryReportImages, AdminImageDto::new);
            bakeryReportImages.forEach(BakeryReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.PRODUCT_ADD_REPORT_IMAGE)) {
            Page<ProductAddReportImage> productAddReportImages = productAddReportImageRepository.findPageByBakeryAndIsRegisteredIsTrue(bakery, pageable);
            adminImages = PageResponseDto.of(productAddReportImages, AdminImageDto::new);
            productAddReportImages.forEach(ProductAddReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.REVIEW_IMAGE)) {
            Page<ReviewImage> reviewImages = reviewImageRepository.findPageByBakeryAndIsRegisteredIsTrue(bakery, pageable);
            adminImages = PageResponseDto.of(reviewImages, AdminImageDto::new);
            reviewImages.forEach(ReviewImage::unNew);
        } else throw new DaedongException(DaedongStatus.ADMIN_IMAGE_TYPE_EXCEPTION);
        return adminImages;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        if (type.equals(AdminBakeryImageType.BAKERY_REPORT_IMAGE)) {
            BakeryReportImage bakeryReportImage = bakeryReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_IMAGE_REPORT_NOT_FOUND));
            String replace = bakeryReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                bakeryReportImageRepository.delete(bakeryReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.PRODUCT_ADD_REPORT_IMAGE)) {
            ProductAddReportImage productAddReportImage = productAddReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND));

            String replace = productAddReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                productAddReportImageRepository.delete(productAddReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.REVIEW_IMAGE)) {
            ReviewImage reviewImage = reviewImageRepository.findByIdAndBakery(imageId, bakery)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_IMAGE_NOT_FOUND));

            String replace = reviewImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else reviewImage.unregister();
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

    @Transactional(rollbackFor = Exception.class)
    public PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<ProductAddReport> contents = productAddReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        PageResponseDto<ProductAddReportDto> productAddReports = PageResponseDto.of(contents, ProductAddReportDto::new);
        contents.forEach(ProductAddReport::unNew);
        return productAddReports;
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerProductAddImage(Long bakeryId, Long reportId, AdminImageRegisterRequest request) {
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

    @Transactional(rollbackFor = Exception.class)
    public PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<BakeryUpdateReport> contents = bakeryUpdateReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        PageResponseDto<BakeryUpdateReportDto> bakeryUpdateReports = PageResponseDto.of(contents, BakeryUpdateReportDto::new);
        contents.forEach(BakeryUpdateReport::unNew);
        return bakeryUpdateReports;
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
    public PageResponseDto<NewReviewDto> getNewReviews(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Review> contents = reviewRepository.findPageByBakeryAndIsHideIsFalseAndIsDeleteIsFalse(bakery, pageable);// TODO N+1
        PageResponseDto<NewReviewDto> newReviews = PageResponseDto.of(contents, NewReviewDto::new);
        contents.forEach(Review::unNew);
        return newReviews;
    }

    @Transactional(rollbackFor = Exception.class)
    public void hideNewReview(Long bakeryId, Long reviewId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Review review = reviewRepository.findByIdAndBakery(reviewId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        review.hide();
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerNewReviewImage(Long bakeryId, Long reviewId, AdminImageRegisterRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Review review = reviewRepository.findByIdAndBakery(reviewId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        request.getImageIdList().forEach(
                id -> {
                    ReviewImage reviewImage = reviewImageRepository.findByIdAndReview(id, review)
                            .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_IMAGE_NOT_FOUND));
                    reviewImage.register();
                }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long bakeryId, Long reviewId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Review review = reviewRepository.findByIdAndBakery(reviewId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        review.delete();
    }

//    @Transactional(rollbackFor = Exception.class)
//    public void deleteBakery(Long bakeryId) { // TODO : casacade
//        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
//        flagBakeryRepository.deleteByBakery(bakery);
//        bakeryUpdateReportRepository.deleteByBakery(bakery);
//        bakeryReportImageRepository.deleteByBakery(bakery);
//        productAddReportRepository.deleteByBakery(bakery);
//        reviewImageRepository.deleteByBakery(bakery);
//        reviewProductRatingRepository.deleteByBakeryId(bakeryId);
//        reviewRepository.findByBakery(bakery).forEach(review -> {
//            reviewReportRepository.deleteByReview(review);
//            reviewViewRepository.deleteByReview(review);
//        });
//        bakeryViewRepository.deleteByBakery(bakery);
//        bakeryRepository.deleteById(bakeryId);
//    }
}
