package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.domain.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
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
    private final UserRepository userRepository;
    private final FlagBakeryRepository flagBakeryRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> findBakeryList(
            String username, BakerySortType sortBy, boolean filterBy,
            Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta) {

        Comparator<BakeryCardDto> comparing;
        if(sortBy.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sortBy.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);

        if (!filterBy) {
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
        else {
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
}
