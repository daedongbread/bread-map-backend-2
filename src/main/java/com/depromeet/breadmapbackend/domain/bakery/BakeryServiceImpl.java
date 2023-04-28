package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
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

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<BakeryCardDto> getBakeryList(
            String oAuthId, BakerySortType sortBy, boolean filterBy,
            Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta) {

        Comparator<BakeryCardDto> comparing;
        if(sortBy.equals(BakerySortType.DISTANCE)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sortBy.equals(BakerySortType.POPULAR)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        if (!filterBy) {
            return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                    .map(bakery -> BakeryCardDto.builder()
                            .bakery(bakery)
                            .flagNum(flagBakeryRepository.countFlagNum(bakery))
                            .rating(Math.floor(bakery.getReviewList()
                                    .stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                                    .map(Review::getAverageRating).collect(Collectors.toList())
                                    .stream().mapToDouble(Double::doubleValue)
                                    .average().orElse(0)*10)/10.0)
                            .reviewNum((int) bakery.getReviewList().stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty()).count())
                            .simpleReviewList(bakery.getReviewList().stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
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
            return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                    .map(bakery -> BakeryCardDto.builder()
                            .bakery(bakery)
                            .flagNum(flagBakeryRepository.countFlagNum(bakery))
                            .rating(Math.floor(bakery.getReviewList()
                                    .stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                                    .map(Review::getAverageRating).collect(Collectors.toList())
                                    .stream().mapToDouble(Double::doubleValue)
                                    .average().orElse(0)*10)/10.0)
                            .reviewNum((int) bakery.getReviewList().stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty()).count())
                            .simpleReviewList(bakery.getReviewList().stream()
                                    .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                                    .sorted(Comparator.comparing(Review::getId).reversed()).map(MapSimpleReviewDto::new)
                                    .limit(3).collect(Collectors.toList()))
                            .distance(floor(acos(cos(toRadians(latitude))
                                    * cos(toRadians(bakery.getLatitude()))
                                    * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                    + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000))
                            .color(flagBakeryRepository.findFlagByBakeryAndUser(bakery, me).isPresent() ?
                                    flagBakeryRepository.findFlagByBakeryAndUser(bakery, me).get().getColor():FlagColor.GRAY)
                            .build())
                    .sorted(comparing)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryDto getBakery(String oAuthId, Long bakeryId) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryViewRepository.findByBakery(bakery)
                .orElseGet(() -> {
                    BakeryView bakeryView = BakeryView.builder().bakery(bakery).build();
                    return bakeryViewRepository.save(bakeryView);
                }).viewBakery();

        BakeryDto.BakeryInfo bakeryInfo = BakeryDto.BakeryInfo.builder()
                .bakery(bakery)
                .flagNum(flagBakeryRepository.countFlagNum(bakery))
                .rating(Math.floor(bakery.getReviewList()
                        .stream()
                        .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                        .map(Review::getAverageRating).collect(Collectors.toList())
                        .stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0)*10)/10.0)
                .reviewNum((int) bakery.getReviewList().stream()
                        .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty()).count())
                .build();
        BakeryDto.FlagInfo flagInfo = BakeryDto.FlagInfo.builder()
                .flagBakery(flagBakeryRepository.findByBakeryAndUser(bakery, me).orElse(null)).build();

        return BakeryDto.builder()
                .bakeryInfo(bakeryInfo).flagInfo(flagInfo).facilityInfoList(bakery.getFacilityInfoList()).build();
    }
}
