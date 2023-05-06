package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
import com.depromeet.breadmapbackend.domain.user.block.BlockUserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryServiceImpl implements BakeryService {
    private final BakeryRepository bakeryRepository;
    private final BakeryViewRepository bakeryViewRepository;
    private final UserRepository userRepository;
    private final BlockUserRepository blockUserRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final ReviewService reviewService;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> getBakeryList(
            String oAuthId, BakerySortType sortBy, boolean filterBy,
            Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta) {

        Comparator<BakeryCardDto> comparing;
        if(sortBy.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sortBy.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        List<Bakery> bakeries = bakeryRepository
                .findTop20ByLatitudeBetweenAndLongitudeBetweenAndStatus(
                        latitude - latitudeDelta / 2, latitude + latitudeDelta / 2,
                        longitude - longitudeDelta / 2, longitude + longitudeDelta / 2,
                        BakeryStatus.POSTING);

        return bakeries.stream()
                .map(bakery -> {
                    List<Review> reviewList = reviewService.getReviewList(me, bakery);
//                    List<Review> reviewList = bakery.getReviewList().stream()
//                            .filter(Review::isValid)
//                            .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
//                            .collect(Collectors.toList());
                    FlagColor color = null;
                    if (!filterBy) color = FlagColor.ORANGE;
                    else color = flagBakeryRepository.findFlagByBakeryAndUser(bakery, me).isPresent() ?
                            flagBakeryRepository.findFlagByBakeryAndUser(bakery, me).get().getColor() : FlagColor.GRAY;
                    return BakeryCardDto.builder()
                            .bakery(bakery)
                            .flagNum(flagBakeryRepository.countFlagNum(bakery))
                            .rating(bakeryRating(reviewList))
                            .reviewNum(reviewList.size())
                            .simpleReviewList(reviewList.stream()
                                    .sorted(Comparator.comparing(Review::getCreatedAt).reversed()).map(MapSimpleReviewDto::new)
                                    .limit(3).collect(Collectors.toList()))
                            .distance(floor(acos(cos(toRadians(latitude))
                                    * cos(toRadians(bakery.getLatitude()))
                                    * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                    + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000))
                            .color(color)
                            .build();
                })
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public BakeryDto getBakery(String oAuthId, Long bakeryId) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findByIdAndStatus(bakeryId, BakeryStatus.POSTING).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryViewRepository.findByBakery(bakery)
                .orElseGet(() -> {
                    BakeryView bakeryView = BakeryView.builder().bakery(bakery).build();
                    return bakeryViewRepository.save(bakeryView);
                }).viewBakery();

//        List<Review> reviewList = bakery.getReviewList().stream()
//                .filter(Review::isValid)
//                .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
//                .collect(Collectors.toList());
        List<Review> reviewList = reviewService.getReviewList(me, bakery);

        BakeryDto.BakeryInfo bakeryInfo = BakeryDto.BakeryInfo.builder()
                .bakery(bakery)
                .flagNum(flagBakeryRepository.countFlagNum(bakery))
                .rating(bakeryRating(reviewList))
                .reviewNum(reviewList.size())
                .build();
        BakeryDto.FlagInfo flagInfo = BakeryDto.FlagInfo.builder()
                .flagBakery(flagBakeryRepository.findByBakeryAndUser(bakery, me).orElse(null)).build();
        BakeryDto.PioneerInfo pioneerInfo = BakeryDto.PioneerInfo.builder().pioneer(bakery.getPioneer()).build();

        return BakeryDto.builder()
                .bakeryInfo(bakeryInfo).flagInfo(flagInfo).facilityInfoList(bakery.getFacilityInfoList()).pioneerInfo(pioneerInfo).build();
    }

    private Double bakeryRating(List<Review> reviewList) {
        return Math.floor(reviewList.stream().map(Review::getAverageRating).collect(Collectors.toList())
                .stream().mapToDouble(Double::doubleValue).average().orElse(0)*10)/10.0;
    }
}
