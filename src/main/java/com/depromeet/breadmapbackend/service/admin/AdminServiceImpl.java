package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryReportNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
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
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;

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
    public BakeryLocationDto getBakeryLatitudeLongitude(String address) throws JsonProcessingException {
//        String URL = "https://sgisapi.kostat.go.kr/OpenAPI3/addr/geocode.json";
//        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(URL);
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
//        WebClient webClient = WebClient.builder()
//                .uriBuilderFactory(factory)
//                .baseUrl(URL)
//                .build();
//        Map<String, Object> response = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .queryParam("accessToken", "")
//                        .queryParam("address", address)
//                        .build())
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
//        ObjectMapper mapper = new ObjectMapper();
//        Map map = mapper.convertValue(response.get("result"), Map.class);
//        Map map1 = mapper.convertValue(((List<String>) map.get("resultdata")).get(0), Map.class);
//        String x = (String) map1.get("x");
//        String y = (String) map1.get("y");
//
//        URL = "https://sgisapi.kostat.go.kr/OpenAPI3/transformation/transcoord.json";
//        factory = new DefaultUriBuilderFactory(URL);
//        webClient = WebClient.builder()
//                .uriBuilderFactory(factory)
//                .baseUrl(URL)
//                .build();
//        Map<String, Object> response2 = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .queryParam("accessToken", "fe9097bd-018a-4ff1-9d10-a992b6f2ef68")
//                        .queryParam("src", 5179)
//                        .queryParam("dst", 4326)
//                        .queryParam("posX", x)
//                        .queryParam("posY", y)
//                        .build())
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
//        Map map3 = mapper.convertValue(response2.get("result"), Map.class);
//        Double posX = (Double) map3.get("posX");
//        Double posY = (Double) map3.get("posY");
////        log.info("X : " + posX);
////        log.info("Y : " + posY);
//        return BakeryLocationDto.builder().latitude(posY).longitude(posX).build();
        return BakeryLocationDto.builder().latitude(3D).longitude(4D).build();
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
