package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.SortType;
import com.depromeet.breadmapbackend.domain.bakery.exception.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.domain.review.repository.BreadReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryInfo;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BreadDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryServiceImpl implements BakeryService {
    private final BakeryRepository bakeryRepository;
    private final BreadRepository breadRepository;
    private final BreadReviewRepository breadReviewRepository;
    private final UserRepository userRepository;
    private final FlagRepository flagRepository;

    @Transactional(readOnly = true)
    public List<BakeryCardDto> findBakeryList
            (Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, SortType sort) {

        Comparator<BakeryCardDto> comparing;
        if(sort.equals(SortType.distance)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sort.equals(SortType.popular)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new SortTypeWrongException();

        return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                .map(bakery -> BakeryCardDto.builder()
                        .bakery(bakery)
                        .rating(Math.floor(Arrays.stream(bakery.getBreadReviewList().stream().map(BreadReview::getRating)
                                .mapToInt(Integer::intValue).toArray()).average().orElse(0)*10)/10.0)
                        .reviewNum(bakery.getBreadReviewList().size())
                        .simpleReviewList(bakery.getBreadReviewList().stream()
                                .sorted(Comparator.comparing(BreadReview::getId)).map(MapSimpleReviewDto::new)
                                .limit(3).collect(Collectors.toList()))
                        .distance(Math.floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BakeryCardDto> findBakeryListByFilter
            (String username, Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, SortType sort) {
        return null;
    }

    @Transactional(readOnly = true)
    public BakeryDto findBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        BakeryInfo info = BakeryInfo.builder()
                .bakery(bakery)
                .rating(Math.floor(Arrays.stream(bakery.getBreadReviewList().stream().map(BreadReview::getRating)
                        .mapToInt(Integer::intValue).toArray()).average().orElse(0)*10)/10.0)
                .reviewNum(bakery.getBreadReviewList().size()).build();
        List<BreadDto> menu = breadRepository.findByBakeryId(bakeryId).stream()
                .map(bread -> new BreadDto(bread,
                        Math.floor(breadReviewRepository.findBreadAvgRating(bread.getId())*10)/10.0,
                        breadReviewRepository.countByBreadId(bread.getId()))).limit(3).collect(Collectors.toList());
        return BakeryDto.builder().info(info).menu(menu).facilityInfoList(bakery.getFacilityInfoList()).build();
    }
}
