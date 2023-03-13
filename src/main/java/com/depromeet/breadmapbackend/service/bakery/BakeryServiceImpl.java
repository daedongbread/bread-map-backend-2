package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.*;
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
    private final UserRepository userRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final BakeryReportImageRepository bakeryReportImageRepository;
    private final ProductAddReportRepository productAddReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> findBakeryList
            (Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sortBy) {

        Comparator<BakeryCardDto> comparing;
        if(sortBy.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sortBy.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
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
            (String username, Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sortBy) {

        Comparator<BakeryCardDto> comparing;
        if(sortBy.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sortBy.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
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
    public void bakeryUpdateReport(String username, Long bakeryId, BakeryUpdateReportRequest request, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        BakeryUpdateReport bakeryUpdateReport = BakeryUpdateReport.builder()
                .bakery(bakery).user(user).reason(BakeryUpdateReason.ETC).content(request.getContent()).build();
        bakeryUpdateReportRepository.save(bakeryUpdateReport);

        if (files != null) {
            if (files.size() > 5) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String imagePath = fileConverter.parseFileInfo(file, ImageType.BAKERY_UPDATE_REPORT_IMAGE, bakeryId);
                String image = s3Uploader.upload(file, imagePath);
                BakeryUpdateReportImage.builder().bakery(bakery).report(bakeryUpdateReport).image(image).build();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void bakeryAddReport(String username, BakeryReportRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        BakeryAddReport bakeryAddReport = BakeryAddReport.builder()
                .name(request.getName()).location(request.getLocation()).content(request.getContent())
                .user(user).build();
        bakeryAddReportRepository.save(bakeryAddReport);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bakeryReportImage(String username, Long bakeryId, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        if (files != null) {
            if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String imagePath = fileConverter.parseFileInfo(file, ImageType.BAKERY_REPORT_IMAGE, bakeryId);
                String image = s3Uploader.upload(file, imagePath);
                bakeryReportImageRepository.save(BakeryReportImage.builder().bakery(bakery).image(image).user(user).build());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void productAddReport(String username, Long bakeryId, ProductReportRequest request, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        ProductAddReport productAddReport = ProductAddReport.builder()
                .bakery(bakery).user(user).name(request.getName()).price(request.getPrice()).build();
        productAddReportRepository.save(productAddReport);

        if (files != null) {
            if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String imagePath = fileConverter.parseFileInfo(file, ImageType.PRODUCT_ADD_REPORT_IMAGE, bakeryId);
                String image = s3Uploader.upload(file, imagePath);
                ProductAddReportImage.builder().productAddReport(productAddReport).image(image).build();
            }
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleProductDto> searchSimpleProductList(Long bakeryId, String name) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        return productRepository.findByBakeryAndNameStartsWith(bakery, name).stream()
                .filter(Product::isTrue)
                .map(SimpleProductDto::new).collect(Collectors.toList());
    }
}
