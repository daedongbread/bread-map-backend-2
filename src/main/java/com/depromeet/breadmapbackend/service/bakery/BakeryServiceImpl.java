package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepositorySupport;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryServiceImpl implements BakeryService {
    private final BakeryRepository bakeryRepository;
    private final ProductRepository productRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FlagRepositorySupport flagRepositorySupport;
    private final FlagBakeryRepository flagBakeryRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryDeleteReportRepository bakeryDeleteReportRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final ProductAddReportRepository productAddReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final CustomAWSS3Properties customAwss3Properties;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> findBakeryList
            (Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sort) {

        Comparator<BakeryCardDto> comparing;
        if(sort.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sort.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);

        return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                .map(bakery -> BakeryCardDto.builder()
                        .bakery(bakery)
                        .rating(Math.floor(bakery.getReviewList()
                                .stream().map(br -> Math.floor(br.getRatings()
                                        .stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue).average()
                                        .orElse(0)*10)/ 10.0).collect(Collectors.toList())
                                .stream().mapToDouble(Double::doubleValue)
                                .average().orElse(0)*10)/10.0)
                        .reviewNum(bakery.getReviewList().size())
                        .simpleReviewList(bakery.getReviewList().stream()
                                .sorted(Comparator.comparing(Review::getId).reversed()).map(MapSimpleReviewDto::new)
                                .limit(3).collect(Collectors.toList()))
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000))
                        .color(FlagColor.ORANGE)
                        .build())
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> findBakeryListByFilter
            (String username, Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sort) {

        Comparator<BakeryCardDto> comparing;
        if(sort.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sort.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                .map(bakery -> BakeryCardDto.builder()
                        .bakery(bakery)
                        .rating(Math.floor(bakery.getReviewList()
                                .stream().map(br -> Math.floor(br.getRatings()
                                        .stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue).average()
                                        .orElse(0)*10)/ 10.0).collect(Collectors.toList())
                                .stream().mapToDouble(Double::doubleValue)
                                .average().orElse(0)*10)/10.0)
                        .reviewNum(bakery.getReviewList().size())
                        .simpleReviewList(bakery.getReviewList().stream()
                                .sorted(Comparator.comparing(Review::getId).reversed()).map(MapSimpleReviewDto::new)
                                .limit(3).collect(Collectors.toList()))
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000))
                        .color(flagBakeryRepository.findFlagByBakeryAndUser(bakery, user).isPresent() ?
                                flagBakeryRepository.findFlagByBakeryAndUser(bakery, user).get().getColor():FlagColor.GRAY)
                        .build())
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryDto findBakery(String username, Long bakeryId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakery.addViews();

        BakeryDto.BakeryInfo bakeryInfo = BakeryDto.BakeryInfo.builder()
                .bakery(bakery)
                .rating(Math.floor(bakery.getReviewList()
                        .stream().map(br -> Math.floor(br.getRatings()
                                .stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue).average()
                                .orElse(0)*10)/ 10.0).collect(Collectors.toList())
                        .stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0)*10)/10.0)
                .reviewNum(bakery.getReviewList().size()).build();
        BakeryDto.FlagInfo flagInfo = BakeryDto.FlagInfo.builder()
                .flagBakery(flagBakeryRepository.findByBakeryAndUser(bakery, user).orElse(null)).build();
//        List<ReviewDto> review = reviewRepository.findByBakery(bakery)
//                .stream().filter(rv -> rv.getStatus().equals(ReviewStatus.UNBLOCK))
//                .map(br -> new ReviewDto(br,
//                        reviewRepository.countByUser(br.getUser()),
//                        followRepository.countByFromUser(br.getUser())
//                ))
//                .sorted(Comparator.comparing(ReviewDto::getId).reversed())
//                .limit(3)
//                .collect(Collectors.toList());

        return BakeryDto.builder()
                .bakeryInfo(bakeryInfo).flagInfo(flagInfo)./*review(review).*/facilityInfoList(bakery.getFacilityInfoList()).build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ProductDto> findProductList(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return productRepository.findByBakery(bakery).stream()
                .filter(Product::isTrue)
                .map(product -> new ProductDto(product,
                        Math.floor(reviewProductRatingRepository.findProductAvgRating(product.getId()).orElse(0D)*10)/10.0, //TODO
                        reviewProductRatingRepository.countByProductId(product.getId())))
                .sorted(Comparator.comparing(ProductDto::getRating).reversed().thenComparing(ProductDto::getName))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void bakeryUpdateReport(Long bakeryId, BakeryUpdateRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        BakeryUpdateReport bakeryUpdateReport = BakeryUpdateReport.builder()
                .bakery(bakery).name(request.getName()).location(request.getLocation()).content(request.getContent()).build();
        bakeryUpdateReportRepository.save(bakeryUpdateReport);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bakeryDeleteReport(Long bakeryId, MultipartFile file) throws IOException {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        String imagePath = fileConverter.parseFileInfo(file, ImageType.BAKERY_DELETE_REPORT_IMAGE, bakeryId);
        String image = s3Uploader.upload(file, imagePath);

        BakeryDeleteReport bakeryDeleteReport = BakeryDeleteReport.builder().bakery(bakery).image(image).build();
        bakeryDeleteReportRepository.save(bakeryDeleteReport);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bakeryAddReport(@CurrentUser String username, BakeryReportRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        BakeryAddReport bakeryAddReport = BakeryAddReport.builder()
                .name(request.getName()).location(request.getLocation()).content(request.getContent())
                .user(user).build();
        bakeryAddReportRepository.save(bakeryAddReport);
    }

    @Transactional(rollbackFor = Exception.class)
    public void productAddReport(Long bakeryId, ProductReportRequest request, List<MultipartFile> files) throws IOException {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        ProductAddReport productAddReport = ProductAddReport.builder()
                .bakery(bakery).name(request.getName()).price(request.getPrice()).build();

        if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
        for (MultipartFile file : files) {
            String imagePath = fileConverter.parseFileInfo(file, ImageType.PRODUCT_ADD_REPORT_IMAGE, bakeryId);
            String image = s3Uploader.upload(file, imagePath);
            productAddReport.addImage(image);
        }
        productAddReportRepository.save(productAddReport);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleProductDto> searchSimpleProductList(Long bakeryId, String name) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return productRepository.findByBakeryAndNameStartsWith(bakery, name).stream()
                .filter(Product::isTrue)
                .map(SimpleProductDto::new).collect(Collectors.toList());
    }
}
