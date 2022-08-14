package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryReportNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.exception.ReviewReportNotFoundException;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.admin.dto.SimpleBakeryAddReportDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final BreadRepository breadRepository;
    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryDeleteReportRepository bakeryDeleteReportRepository;
    private final BreadAddReportRepository breadAddReportRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final FlagBakeryRepository flagBakeryRepository;

    @Value("${sgis.key}")
    public String SGIS_CONSUMER_KEY;

    @Value("${sgis.secret}")
    private String SGIS_CONSUMER_SECRET;

    @Transactional(readOnly = true)
    public AdminBakeryListDto getBakeryList(Pageable pageable) {
        Page<Bakery> all = bakeryRepository.findAll(pageable);
        List<AdminSimpleBakeryDto> dtoList = all.stream().map(AdminSimpleBakeryDto::new).collect(Collectors.toList());
        int totalNum = (int) all.getTotalElements();
        return AdminBakeryListDto.builder().bakeryDtoList(dtoList).totalNum(totalNum).build();
    }

    @Transactional(readOnly = true)
    public AdminBakeryDto getBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        List<AdminBreadDto> menu = breadRepository.findByBakery(bakery).stream()
                .filter(Bread::isTrue)
                .map(AdminBreadDto::new).collect(Collectors.toList());
        return AdminBakeryDto.builder().bakery(bakery).menu(menu).build();
    }

    @Transactional(readOnly = true)
    public AdminBakeryListDto searchBakeryList(String name, Pageable pageable) {
        Page<Bakery> all = bakeryRepository.findByNameContains(name, pageable);
        List<AdminSimpleBakeryDto> dtoList = all.stream().map(AdminSimpleBakeryDto::new).collect(Collectors.toList());
        int totalNum = (int) all.getTotalElements();
        return AdminBakeryListDto.builder().bakeryDtoList(dtoList).totalNum(totalNum).build();
    }

    @Transactional(readOnly = true)
    public BakeryLocationDto getBakeryLatitudeLongitude(String address) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String auth_url = "https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(auth_url);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        WebClient webClient = WebClient.builder().uriBuilderFactory(factory).baseUrl(auth_url).build();
        Map accessResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("consumer_key", SGIS_CONSUMER_KEY)
                        .queryParam("consumer_secret", SGIS_CONSUMER_SECRET)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Map map = mapper.convertValue(accessResponse.get("result"), Map.class);
        String accessToken = (String) map.get("accessToken");

        String geocode_url = "https://sgisapi.kostat.go.kr/OpenAPI3/addr/geocode.json";
        factory = new DefaultUriBuilderFactory(geocode_url);
        webClient = WebClient.builder().uriBuilderFactory(factory).baseUrl(geocode_url).build();
        Map geoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("accessToken", accessToken)
                        .queryParam("address", address)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        map = mapper.convertValue(geoResponse.get("result"), Map.class);
        map = mapper.convertValue(((List<String>) map.get("resultdata")).get(0), Map.class);
        String x = (String) map.get("x");
        String y = (String) map.get("y");

        String trans_url = "https://sgisapi.kostat.go.kr/OpenAPI3/transformation/transcoord.json";
        factory = new DefaultUriBuilderFactory(trans_url);
        webClient = WebClient.builder().uriBuilderFactory(factory).baseUrl(trans_url).build();
        Map transResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("accessToken", accessToken)
                        .queryParam("src", 5179)
                        .queryParam("dst", 4326)
                        .queryParam("posX", x)
                        .queryParam("posY", y)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        map = mapper.convertValue(transResponse.get("result"), Map.class);
        Double posX = (Double) map.get("posX");
        Double posY = (Double) map.get("posY");
//        log.info("X : " + posX);
//        log.info("Y : " + posY);
        return BakeryLocationDto.builder().latitude(posY).longitude(posX).build();
//        return BakeryLocationDto.builder().latitude(3D).longitude(4D).build();
    }

    @Transactional
    public void addBakery(AddBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) {
        //TODO : 사진
//        Bakery bakery = Bakery.builder()
//                .id(request.getBakeryId()).name(request.getName()).image()
//                .address(request.getAddress()).latitude(request.getLatitude()).longitude(request.getLongitude())
//                .hours(request.getHours())
//                .websiteURL(request.getWebsiteURL()).instagramURL(request.getInstagramURL()).facebookURL(request.getFacebookURL()).blogURL(request.getBlogURL())
//                .phoneNumber(request.getPhoneNumber())
//                .facilityInfoList(request.getFacilityInfoList())
//                .status(request.getStatus())
//                .build();
//        bakeryRepository.save(bakery);
//
//        request.getBreadList().forEach(addBreadRequest -> {
//
//            Bread bread = Bread.builder().bakery(bakery)
//                    .name(addBreadRequest.getName()).price(addBreadRequest.getPrice()).image()
//                    .build();
//            breadRepository.save(bread);
//        });
    }

    @Transactional
    public void updateBakery(Long bakeryId, UpdateBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) {

    }

    @Transactional
    public void deleteBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        flagBakeryRepository.deleteByBakery(bakery);
        bakeryDeleteReportRepository.deleteByBakery(bakery);
        bakeryUpdateReportRepository.deleteByBakery(bakery);
        breadAddReportRepository.deleteByBakery(bakery);
        reviewRepository.findByBakery(bakery).forEach(reviewReportRepository::deleteByReview);
        bakeryRepository.deleteById(bakeryId);
    }

    @Transactional(readOnly = true)
    public BakeryAddReportListDto getBakeryReportList(Pageable pageable) {
        Page<BakeryAddReport> all = bakeryAddReportRepository.findAll(pageable);
        List<SimpleBakeryAddReportDto> dtoList = all.stream().map(SimpleBakeryAddReportDto::new).collect(Collectors.toList());
        int totalNum = (int) all.getTotalElements();
        return BakeryAddReportListDto.builder().bakeryAddReportDtoList(dtoList).totalNum(totalNum).build();
    }

    @Transactional(readOnly = true)
    public BakeryAddReportDto getBakeryReport(Long reportId) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        return BakeryAddReportDto.builder().bakeryAddReport(bakeryAddReport).build();
    }

    @Transactional
    public void updateBakeryAddReportStatus(Long reportId, UpdateBakeryReportStatusRequest request) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(BakeryReportNotFoundException::new);
        bakeryAddReport.updateStatus(request.getStatus());
    }

    @Transactional(readOnly = true)
    public AdminReviewReportListDto getReviewReportList(Pageable pageable) {
        Page<ReviewReport> all = reviewReportRepository.findAll(pageable);
        List<AdminReviewReportDto> dtoList = all.stream().map(AdminReviewReportDto::new).collect(Collectors.toList());
        int totalNum = (int) all.getTotalElements();
        return AdminReviewReportListDto.builder().reviewReportDtoList(dtoList).totalNum(totalNum).build();
    }

    @Transactional
    public void updateReviewStatus(Long reportId) {
        ReviewReport reviewReport = reviewReportRepository.findById(reportId).orElseThrow(ReviewReportNotFoundException::new);
        reviewReport.getReview().useChange();
    }

    @Transactional(readOnly = true)
    public AdminUserListDto getUserList(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        List<AdminUserDto> dtoList = all.stream().map(AdminUserDto::new).collect(Collectors.toList());
        int totalNum = (int) all.getTotalElements();
        return AdminUserListDto.builder().userDtoList(dtoList).totalNum(totalNum).build();
    }

    @Transactional
    public void changeUserBlock(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.changeBlock();
    }
}
