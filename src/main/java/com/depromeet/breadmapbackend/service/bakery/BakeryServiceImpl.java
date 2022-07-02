package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.exception.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.common.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageFolderPath;
import com.depromeet.breadmapbackend.domain.exception.ImageNotExistException;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepositorySupport;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.repository.BreadRatingRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
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
    private final BreadRepository breadRepository;
    private final BreadRatingRepository breadRatingRepository;
    private final UserRepository userRepository;
    private final FlagRepositorySupport flagRepositorySupport;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryDeleteReportRepository bakeryDeleteReportRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final BreadAddReportRepository breadAddReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public List<BakeryCardDto> findBakeryList
            (Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sort) {

        Comparator<BakeryCardDto> comparing;
        if(sort.equals(BakerySortType.distance)) comparing = Comparator.comparing(BakeryCardDto::getDistance);
        else if(sort.equals(BakerySortType.popular)) comparing = Comparator.comparing(BakeryCardDto::getPopularNum).reversed();
        else throw new SortTypeWrongException();

        return bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude-latitudeDelta/2, latitude+latitudeDelta/2, longitude-longitudeDelta/2, longitude+longitudeDelta/2).stream()
                .map(bakery -> BakeryCardDto.builder()
                        .bakery(bakery)
                        .rating(Math.floor(bakery.getReviewList()
                                .stream().map(br -> Math.floor(br.getRatings()
                                        .stream().map(BreadRating::getRating).mapToLong(Long::longValue).average()
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
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BakeryFilterCardDto> findBakeryListByFilter
            (String username, Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta, BakerySortType sort) {

        Comparator<BakeryFilterCardDto> comparing;
        if(sort.equals(BakerySortType.distance)) comparing = Comparator.comparing(BakeryFilterCardDto::getDistance);
        else if(sort.equals(BakerySortType.popular)) comparing = Comparator.comparing(BakeryFilterCardDto::getPopularNum).reversed();
        else throw new SortTypeWrongException();

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        List<Bakery> bakeryList = bakeryRepository.findTop20ByLatitudeBetweenAndLongitudeBetween(latitude - latitudeDelta / 2, latitude + latitudeDelta / 2, longitude - longitudeDelta / 2, longitude + longitudeDelta / 2).stream()
                .filter(bakery -> flagRepositorySupport.existFlagBakeryByUserAndBakery(user, bakery))
                .collect(Collectors.toList());

        List<BakeryFilterCardDto> bakeryFilterCardDtoList = new ArrayList<>();
        for(Bakery b : bakeryList) {
            FlagBakery flagBakery = flagRepositorySupport.findFlagBakeryByUserAndBakery(user, b);
            Bakery bakery = flagBakery.getBakery();
            bakeryFilterCardDtoList.add(BakeryFilterCardDto.builder()
                    .bakery(bakery)
                    .rating(Math.floor(bakery.getReviewList()
                            .stream().map(br -> Math.floor(br.getRatings()
                                    .stream().map(BreadRating::getRating).mapToLong(Long::longValue).average()
                                    .orElse(0)*10)/ 10.0).collect(Collectors.toList())
                            .stream().mapToDouble(Double::doubleValue)
                            .average().orElse(0)*10)/10.0)
                    .reviewNum(bakery.getReviewList().size())
                    .simpleReviewList(bakery.getReviewList().stream()
                            .sorted(Comparator.comparing(Review::getId).reversed()).map(MapSimpleReviewDto::new)
                            .limit(3).collect(Collectors.toList()))
                    .distance(Math.floor(acos(cos(toRadians(latitude))
                            * cos(toRadians(bakery.getLatitude()))
                            * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                            + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000))
                    .color(flagBakery.getFlag().getColor()).build());
        }
        return bakeryFilterCardDtoList.stream()
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BakeryDto findBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        BakeryInfo info = BakeryInfo.builder()
                .bakery(bakery)
                .rating(Math.floor(bakery.getReviewList()
                        .stream().map(br -> Math.floor(br.getRatings()
                                .stream().map(BreadRating::getRating).mapToLong(Long::longValue).average()
                                .orElse(0)*10)/ 10.0).collect(Collectors.toList())
                        .stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0)*10)/10.0)
                .reviewNum(bakery.getReviewList().size()).build();
        List<BreadDto> menu = breadRepository.findByBakeryId(bakeryId).stream()
                .map(bread -> new BreadDto(bread,
                        Math.floor(breadRatingRepository.findBreadAvgRating(bread.getId())*10)/10.0, //TODO
                        breadRatingRepository.countByBreadId(bread.getId()))).limit(3).collect(Collectors.toList());
        return BakeryDto.builder().info(info).menu(menu).facilityInfoList(bakery.getFacilityInfoList()).build();
    }

    @Transactional
    public void bakeryUpdateReport(Long bakeryId, BakeryUpdateRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        BakeryUpdateReport bakeryUpdateReport = BakeryUpdateReport.builder()
                .bakery(bakery).name(request.getName()).location(request.getLocation()).content(request.getContent()).build();
        bakeryUpdateReportRepository.save(bakeryUpdateReport);
    }

    @Transactional
    public void bakeryDeleteReport(Long bakeryId, MultipartFile file) throws IOException {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);

        if(file.isEmpty()) throw new ImageNotExistException();

        String imagePath = fileConverter.parseFileInfo(file, ImageFolderPath.bakeryDeleteReportImage, bakeryId);
        String image = s3Uploader.upload(file, imagePath);

        BakeryDeleteReport bakeryDeleteReport = BakeryDeleteReport.builder().bakery(bakery).image(image).build();
        bakeryDeleteReportRepository.save(bakeryDeleteReport);
    }

    @Transactional
    public void bakeryAddReport(BakeryReportRequest request) {
        BakeryAddReport bakeryAddReport = BakeryAddReport.builder()
                .name(request.getName()).location(request.getLocation()).content(request.getContent()).build();
        bakeryAddReportRepository.save(bakeryAddReport);
    }

    @Transactional
    public void breadAddReport(Long bakeryId, BreadReportRequest request, List<MultipartFile> files) throws IOException {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);

        BreadAddReport breadAddReport = BreadAddReport.builder()
                .bakery(bakery).name(request.getName()).price(request.getPrice()).build();
        for (MultipartFile file : files) {
            String imagePath = fileConverter.parseFileInfo(file, ImageFolderPath.breadAddReportImage, bakeryId);
            String image = s3Uploader.upload(file, imagePath);
            breadAddReport.addImage(image);
        }
        breadAddReportRepository.save(breadAddReport);
    }
}
