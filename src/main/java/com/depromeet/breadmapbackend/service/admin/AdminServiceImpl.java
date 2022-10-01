package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryIdAlreadyException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryReportNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.exception.BreadNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.exception.AdminJoinException;
import com.depromeet.breadmapbackend.domain.exception.ImageNumExceedException;
import com.depromeet.breadmapbackend.domain.exception.ImageNumMatchException;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.exception.ReviewReportNotFoundException;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewImageRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.admin.dto.SimpleBakeryAddReportDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
    private final ReviewImageRepository reviewImageRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${sgis.key}")
    public String SGIS_CONSUMER_KEY;

    @Value("${sgis.secret}")
    private String SGIS_CONSUMER_SECRET;

    @Value("${spring.jwt.admin}")
    private String JWT_ADMIN_KEY;

    @Value("${spring.redis.key.refresh}")
    private String REDIS_KEY_REFRESH;

    @Transactional
    public void adminJoin(AdminJoinRequest request) {
        if(userRepository.findByEmail(request.getAdminId()).isPresent()) throw new AdminJoinException();
        if(!request.getSecret().equals(JWT_ADMIN_KEY)) throw new AdminJoinException();
        User admin = User.builder().email(request.getAdminId())
                .username(passwordEncoder.encode(request.getPassword()))
                .roleType(RoleType.ADMIN).build();
        userRepository.save(admin);
    }

    @Transactional
    public JwtToken reissue(ReissueRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new TokenValidFailedException();

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        String refreshToken = redisTemplate.opsForValue().get(REDIS_KEY_REFRESH + user.getUsername());
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken())) throw new TokenValidFailedException();
//        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(RefreshTokenNotFoundException::new);
//        if(!refreshToken.getToken().equals(request.getRefreshToken())) throw new TokenValidFailedException();

        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + user.getUsername(),
                        reissueToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
//        refreshToken.updateToken(reissueToken.getRefreshToken());

        return reissueToken;
    }

    @Transactional
    public JwtToken adminLogin(AdminLoginRequest request) {
        User user = userRepository.findByEmail(request.getAdminId()).orElseThrow(UserNotFoundException::new);
        log.info("PW : " + user.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getUsername())) throw new UserNotFoundException();

        JwtToken adminToken = jwtTokenProvider.createJwtToken(user.getUsername(), "ROLE_ADMIN");
        redisTemplate.opsForValue()
                .set("RT:" + user.getId(),
                        adminToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
        return adminToken;
    }

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
        log.info("RAS : " + accessResponse);
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
        return BakeryLocationDto.builder().latitude(posY).longitude(posX).build();
    }

    private Long createBakeryId(String address) {
        String bakeryId = null;
        if(address.contains("경기도")) {
            if(address.contains("수원시")) bakeryId = "3740000";
            else if(address.contains("성남시")) bakeryId = "3780000";
            else if(address.contains("의정부시")) bakeryId = "3820000";
            else if(address.contains("안양시")) bakeryId = "3830000";
            else if(address.contains("부천시")) bakeryId = "3860000";
            else if(address.contains("광명시")) bakeryId = "3900000";
            else if(address.contains("평택시")) bakeryId = "3910000";
            else if(address.contains("동두천")) bakeryId = "3920000";
            else if(address.contains("안산시")) bakeryId = "3930000";
            else if(address.contains("고양시")) bakeryId = "3940000";
            else if(address.contains("과천시")) bakeryId = "3970000";
            else if(address.contains("구리시")) bakeryId = "3980000";
            else if(address.contains("남양주")) bakeryId = "3990000";
            else if(address.contains("오산시")) bakeryId = "4000000";
            else if(address.contains("시흥시")) bakeryId = "4010000";
            else if(address.contains("군포시")) bakeryId = "4020000";
            else if(address.contains("의왕시")) bakeryId = "4030000";
            else if(address.contains("하남시")) bakeryId = "4040000";
            else if(address.contains("용인시")) bakeryId = "4050000";
            else if(address.contains("파주시")) bakeryId = "4060000";
            else if(address.contains("이천시")) bakeryId = "4070000";
            else if(address.contains("안성시")) bakeryId = "4080000";
            else if(address.contains("김포시")) bakeryId = "4090000";
            else if(address.contains("연천군")) bakeryId = "4140000";
            else if(address.contains("가평군")) bakeryId = "4160000";
            else if(address.contains("양평군")) bakeryId = "4170000";
            else if(address.contains("화성시")) bakeryId = "5530000";
            else if(address.contains("광주시")) bakeryId = "5540000";
            else if(address.contains("양주시")) bakeryId = "5590000";
            else if(address.contains("포천시")) bakeryId = "5600000";
            else if(address.contains("여주시")) bakeryId = "5700000";
        }
        else if(address.contains("강원도")) {
            if(address.contains("춘천시")) bakeryId = "4180000";
            else if(address.contains("원주시")) bakeryId = "4190000";
            else if(address.contains("강릉시")) bakeryId = "4200000";
            else if(address.contains("동해시")) bakeryId = "4210000";
            else if(address.contains("태백시")) bakeryId = "4220000";
            else if(address.contains("속초시")) bakeryId = "4230000";
            else if(address.contains("삼척시")) bakeryId = "4240000";
            else if(address.contains("홍천군")) bakeryId = "4250000";
            else if(address.contains("횡성군")) bakeryId = "4260000";
            else if(address.contains("영월군")) bakeryId = "4270000";
            else if(address.contains("평창군")) bakeryId = "4280000";
            else if(address.contains("정선군")) bakeryId = "4290000";
            else if(address.contains("철원군")) bakeryId = "4300000";
            else if(address.contains("화천군")) bakeryId = "4310000";
            else if(address.contains("양구군")) bakeryId = "4320000";
            else if(address.contains("인제군")) bakeryId = "4330000";
            else if(address.contains("고성군")) bakeryId = "4340000";
            else if(address.contains("양양군")) bakeryId = "4350000";
        }
        else if(address.contains("충청북도")) {
            if(address.contains("충주시")) bakeryId = "4390000";
            else if(address.contains("제천시")) bakeryId = "4400000";
            else if(address.contains("보은군")) bakeryId = "4420000";
            else if(address.contains("옥천군")) bakeryId = "4430000";
            else if(address.contains("영동군")) bakeryId = "4440000";
            else if(address.contains("진천군")) bakeryId = "4450000";
            else if(address.contains("괴산군")) bakeryId = "4460000";
            else if(address.contains("음성군")) bakeryId = "4470000";
            else if(address.contains("단양군")) bakeryId = "4480000";
            else if(address.contains("증평군")) bakeryId = "5570000";
            else if(address.contains("청주시")) bakeryId = "5710000";
        }
        else if(address.contains("충청남도")) {
            if(address.contains("천안시")) bakeryId = "4490000";
            else if(address.contains("공주시")) bakeryId = "4500000";
            else if(address.contains("보령시")) bakeryId = "4510000";
            else if(address.contains("아산시")) bakeryId = "4520000";
            else if(address.contains("서산시")) bakeryId = "4530000";
            else if(address.contains("논산시")) bakeryId = "4540000";
            else if(address.contains("금산시")) bakeryId = "4550000";
            else if(address.contains("연기군")) bakeryId = "4560000";
            else if(address.contains("부여군")) bakeryId = "4570000";
            else if(address.contains("서천군")) bakeryId = "4580000";
            else if(address.contains("청양군")) bakeryId = "4590000";
            else if(address.contains("홍성군")) bakeryId = "4600000";
            else if(address.contains("예산군")) bakeryId = "4610000";
            else if(address.contains("태안군")) bakeryId = "4620000";
            else if(address.contains("계룡시")) bakeryId = "5580000";
            else if(address.contains("당진시")) bakeryId = "5680000";
        }
        else if(address.contains("전라북도")) {
            if(address.contains("전주시")) bakeryId = "4640000";
            else if(address.contains("군산시")) bakeryId = "4670000";
            else if(address.contains("익산시")) bakeryId = "4680000";
            else if(address.contains("정읍시")) bakeryId = "4690000";
            else if(address.contains("남원시")) bakeryId = "4700000";
            else if(address.contains("김제시")) bakeryId = "4710000";
            else if(address.contains("완주군")) bakeryId = "4720000";
            else if(address.contains("진안군")) bakeryId = "4730000";
            else if(address.contains("무주군")) bakeryId = "4740000";
            else if(address.contains("장수군")) bakeryId = "4750000";
            else if(address.contains("임실군")) bakeryId = "4760000";
            else if(address.contains("순창군")) bakeryId = "4770000";
            else if(address.contains("고창군")) bakeryId = "4780000";
            else if(address.contains("부안군")) bakeryId = "4790000";
        }
        else if(address.contains("전라남도")) {
            if(address.contains("목포시")) bakeryId = "4800000";
            else if(address.contains("여수시")) bakeryId = "4810000";
            else if(address.contains("순천시")) bakeryId = "4820000";
            else if(address.contains("나주시")) bakeryId = "4830000";
            else if(address.contains("광양시")) bakeryId = "4840000";
            else if(address.contains("담양군")) bakeryId = "4850000";
            else if(address.contains("곡성군")) bakeryId = "4860000";
            else if(address.contains("구례군")) bakeryId = "4870000";
            else if(address.contains("고흥군")) bakeryId = "4880000";
            else if(address.contains("보성군")) bakeryId = "4890000";
            else if(address.contains("화순군")) bakeryId = "4900000";
            else if(address.contains("장흥군")) bakeryId = "4910000";
            else if(address.contains("강진군")) bakeryId = "4920000";
            else if(address.contains("해남군")) bakeryId = "4930000";
            else if(address.contains("영암군")) bakeryId = "4940000";
            else if(address.contains("무안군")) bakeryId = "4950000";
            else if(address.contains("함평군")) bakeryId = "4960000";
            else if(address.contains("영광군")) bakeryId = "4970000";
            else if(address.contains("장성군")) bakeryId = "4980000";
            else if(address.contains("완도군")) bakeryId = "4990000";
            else if(address.contains("진도군")) bakeryId = "5000000";
            else if(address.contains("신안군")) bakeryId = "5010000";
        }
        else if(address.contains("경상북도")) {
            if(address.contains("포항시")) bakeryId = "5020000";
            else if(address.contains("경주시")) bakeryId = "5050000";
            else if(address.contains("김천시")) bakeryId = "5060000";
            else if(address.contains("안동시")) bakeryId = "5070000";
            else if(address.contains("구미시")) bakeryId = "5080000";
            else if(address.contains("영주시")) bakeryId = "5090000";
            else if(address.contains("영천시")) bakeryId = "5100000";
            else if(address.contains("상주시")) bakeryId = "5110000";
            else if(address.contains("문경시")) bakeryId = "5120000";
            else if(address.contains("경산시")) bakeryId = "5130000";
            else if(address.contains("군위군")) bakeryId = "5140000";
            else if(address.contains("의성군")) bakeryId = "5150000";
            else if(address.contains("청송군")) bakeryId = "5160000";
            else if(address.contains("영양군")) bakeryId = "5170000";
            else if(address.contains("영덕군")) bakeryId = "5180000";
            else if(address.contains("청도군")) bakeryId = "5190000";
            else if(address.contains("고령군")) bakeryId = "5200000";
            else if(address.contains("성주군")) bakeryId = "5210000";
            else if(address.contains("칠곡군")) bakeryId = "5220000";
            else if(address.contains("예천군")) bakeryId = "5230000";
            else if(address.contains("봉화군")) bakeryId = "5240000";
            else if(address.contains("울진군")) bakeryId = "5250000";
            else if(address.contains("울릉군")) bakeryId = "5260000";
        }
        else if(address.contains("경상남도")) {
            if(address.contains("진주시")) bakeryId = "5310000";
            else if(address.contains("통영시")) bakeryId = "5330000";
            else if(address.contains("사천시")) bakeryId = "5340000";
            else if(address.contains("김해시")) bakeryId = "5350000";
            else if(address.contains("밀양시")) bakeryId = "5360000";
            else if(address.contains("거제시")) bakeryId = "5370000";
            else if(address.contains("양산시")) bakeryId = "5380000";
            else if(address.contains("의령군")) bakeryId = "5390000";
            else if(address.contains("함안군")) bakeryId = "5400000";
            else if(address.contains("창녕군")) bakeryId = "5410000";
            else if(address.contains("고성군")) bakeryId = "5420000";
            else if(address.contains("남해군")) bakeryId = "5430000";
            else if(address.contains("하동군")) bakeryId = "5440000";
            else if(address.contains("산청군")) bakeryId = "5450000";
            else if(address.contains("함양군")) bakeryId = "5460000";
            else if(address.contains("거창군")) bakeryId = "5470000";
            else if(address.contains("합천군")) bakeryId = "5480000";
            else if(address.contains("창원시")) bakeryId = "5670000";
        }
        else if(address.contains("제주특별자치도") || address.contains("제주도")) {
            if(address.contains("제주시")) bakeryId = "6510000";
            else if(address.contains("서귀포시")) bakeryId = "6520000";
        }
        else if(address.contains("서울특별시") || address.contains("서울시")) {
            if(address.contains("종로구")) bakeryId = "3000000";
            else if(address.contains("중구")) bakeryId = "3010000";
            else if(address.contains("용산구")) bakeryId = "3020000";
            else if(address.contains("성동구")) bakeryId = "3030000";
            else if(address.contains("광진구")) bakeryId = "3040000";
            else if(address.contains("동대문구")) bakeryId = "3050000";
            else if(address.contains("중랑구")) bakeryId = "3060000";
            else if(address.contains("성북구")) bakeryId = "3070000";
            else if(address.contains("강북구")) bakeryId = "3080000";
            else if(address.contains("도봉구")) bakeryId = "3090000";
            else if(address.contains("노원구")) bakeryId = "3100000";
            else if(address.contains("은평구")) bakeryId = "3110000";
            else if(address.contains("서대문구")) bakeryId = "3120000";
            else if(address.contains("마포구")) bakeryId = "3130000";
            else if(address.contains("양천구")) bakeryId = "3140000";
            else if(address.contains("강서구")) bakeryId = "3150000";
            else if(address.contains("구로구")) bakeryId = "3160000";
            else if(address.contains("금천구")) bakeryId = "3170000";
            else if(address.contains("영등포구")) bakeryId = "3180000";
            else if(address.contains("동작구")) bakeryId = "3190000";
            else if(address.contains("관악구")) bakeryId = "3200000";
            else if(address.contains("서초구")) bakeryId = "3210000";
            else if(address.contains("강남구")) bakeryId = "3220000";
            else if(address.contains("송파구")) bakeryId = "3230000";
            else if(address.contains("강동구")) bakeryId = "3240000";
        }
        else if(address.contains("부산광역시") || address.contains("부산시")) {
            if(address.contains("중구")) bakeryId = "3250000";
            else if(address.contains("서구")) bakeryId = "3260000";
            else if(address.contains("동구")) bakeryId = "3270000";
            else if(address.contains("영도구")) bakeryId = "3280000";
            else if(address.contains("부산진구")) bakeryId = "3290000";
            else if(address.contains("동래구")) bakeryId = "3300000";
            else if(address.contains("남구")) bakeryId = "3310000";
            else if(address.contains("북구")) bakeryId = "3320000";
            else if(address.contains("해운대구")) bakeryId = "3330000";
            else if(address.contains("사하구")) bakeryId = "3340000";
            else if(address.contains("금정구")) bakeryId = "3350000";
            else if(address.contains("강서구")) bakeryId = "3360000";
            else if(address.contains("연제구")) bakeryId = "3370000";
            else if(address.contains("수영구")) bakeryId = "3380000";
            else if(address.contains("사상구")) bakeryId = "3390000";
            else if(address.contains("기장군")) bakeryId = "3400000";
        }
        else if(address.contains("대구광역시") || address.contains("대구시")) {
            if(address.contains("중구")) bakeryId = "3410000";
            else if(address.contains("동구")) bakeryId = "3420000";
            else if(address.contains("서구")) bakeryId = "3430000";
            else if(address.contains("남구")) bakeryId = "3440000";
            else if(address.contains("북구")) bakeryId = "3450000";
            else if(address.contains("수성구")) bakeryId = "3460000";
            else if(address.contains("달서구")) bakeryId = "3470000";
            else if(address.contains("달성군")) bakeryId = "3480000";

        }
        else if(address.contains("인천광역시") || address.contains("인천시")) {
            if(address.contains("중구")) bakeryId = "3490000";
            else if(address.contains("동구")) bakeryId = "3500000";
            else if(address.contains("미추홀구")) bakeryId = "3510500";
            else if(address.contains("연수구")) bakeryId = "3520000";
            else if(address.contains("남동구")) bakeryId = "3530000";
            else if(address.contains("부평구")) bakeryId = "3540000";
            else if(address.contains("계양구")) bakeryId = "3550000";
            else if(address.contains("서구")) bakeryId = "3560000";
            else if(address.contains("강화군")) bakeryId = "3570000";
            else if(address.contains("옹진군")) bakeryId = "3580000";

        }
        else if(address.contains("광주광역시") || address.contains("광주시")) {
            if(address.contains("동구")) bakeryId = "359";
            else if(address.contains("서구")) bakeryId = "3600000";
            else if(address.contains("남구")) bakeryId = "3610000";
            else if(address.contains("북구")) bakeryId = "3620000";
            else if(address.contains("광산구")) bakeryId = "3630000";
        }
        else if(address.contains("대전광역시") || address.contains("대전시")) {
            if(address.contains("동구")) bakeryId = "3640000";
            else if(address.contains("중구")) bakeryId = "3650000";
            else if(address.contains("서구")) bakeryId = "3660000";
            else if(address.contains("유성구")) bakeryId = "3670000";
            else if(address.contains("대덕구")) bakeryId = "3680000";

        }
        else if(address.contains("울산광역시") || address.contains("울산시")) {
            if(address.contains("중구")) bakeryId = "3690000";
            else if(address.contains("남구")) bakeryId = "3700000";
            else if(address.contains("동구")) bakeryId = "3710000";
            else if(address.contains("북구")) bakeryId = "3720000";
            else if(address.contains("울주군")) bakeryId = "3730000";

        }
        else if(address.contains("세종특별자치시") || address.contains("세종시")) {
            bakeryId = "5690000";
        }
        else bakeryId = "9999999";

        Random rand = new Random();
        do {
            String random = "";
            for(int i = 0; i < 5; i++) {
                random += Integer.toString(rand.nextInt(9));
            }
            // 자치단체코드 7자리, 년도 뒷 2자리, 번호 5자리 -> 14자리
            String year = String.valueOf(LocalDateTime.now().getYear());
            bakeryId = bakeryId + year.substring(2) + random;
        } while(bakeryRepository.findById(Long.parseLong(bakeryId)).isPresent());
        return Long.valueOf(bakeryId);
    }

    @Transactional
    public void addBakery(AddBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) throws IOException {
        Long bakeryId = createBakeryId(request.getAddress());
        Bakery bakery = Bakery.builder()
                .id(bakeryId).name(request.getName())
                .address(request.getAddress()).latitude(request.getLatitude()).longitude(request.getLongitude())
                .hours(request.getHours())
                .websiteURL(request.getWebsiteURL()).instagramURL(request.getInstagramURL()).facebookURL(request.getFacebookURL()).blogURL(request.getBlogURL())
                .phoneNumber(request.getPhoneNumber())
                .facilityInfoList(request.getFacilityInfoList())
                .status(request.getStatus())
                .build();
        bakeryRepository.save(bakery);

        if(bakeryImage != null && !bakeryImage.isEmpty()) {
            String imagePath = fileConverter.parseFileInfo(bakeryImage, ImageType.BAKERY_IMAGE, bakery.getId());
            String image = s3Uploader.upload(bakeryImage, imagePath);
            bakery.updateImage(image);
        }

        if(request.getBreadList().size() != breadImageList.size()) throw new ImageNumMatchException();
        if (breadImageList.size() > 10) throw new ImageNumExceedException();
        for(int i = 0; i < request.getBreadList().size(); i++) {
            AddBakeryRequest.AddBreadRequest addBreadRequest = request.getBreadList().get(i);
            Bread bread = Bread.builder().bakery(bakery)
                    .name(addBreadRequest.getName()).price(addBreadRequest.getPrice()).build();
            breadRepository.save(bread);

            MultipartFile breadImage = breadImageList.get(i);
            if(breadImage != null && !breadImage.isEmpty()) {
                String imagePath = fileConverter.parseFileInfo(breadImage, ImageType.BREAD_IMAGE, bread.getId());
                String image = s3Uploader.upload(breadImage, imagePath);
                bread.updateImage(image);
            }
        }
    }

    @Transactional
    public void updateBakery(Long bakeryId, UpdateBakeryRequest request, MultipartFile bakeryImage, List<MultipartFile> breadImageList) throws IOException {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        if(!bakeryId.equals(request.getBakeryId()) && bakeryRepository.findById(request.getBakeryId()).isPresent())
            throw new BakeryIdAlreadyException();
        bakery.update(request.getBakeryId(), request.getName(),
                request.getAddress(), request.getLatitude(), request.getLongitude(), request.getHours(),
                request.getWebsiteURL(), request.getInstagramURL(), request.getFacebookURL(), request.getBlogURL(),
                request.getPhoneNumber(), request.getFacilityInfoList(), request.getStatus());

        if(bakeryImage != null && !bakeryImage.isEmpty()) {
            if(bakery.getImage() != null) s3Uploader.deleteFileS3(bakery.getImage());
            String imagePath = fileConverter.parseFileInfo(bakeryImage, ImageType.BAKERY_IMAGE, bakery.getId());
            String image = s3Uploader.upload(bakeryImage, imagePath);
            bakery.updateImage(image);
        }

        if(request.getBreadList().size() != breadImageList.size()) throw new ImageNumMatchException();
        if (breadImageList.size() > 10) throw new ImageNumExceedException();
        for(int i = 0; i < request.getBreadList().size(); i++) {
            UpdateBakeryRequest.UpdateBreadRequest updateBreadRequest = request.getBreadList().get(i);
            Bread bread = breadRepository.findById(updateBreadRequest.getBreadId()).orElseThrow(BreadNotFoundException::new);
            bread.update(updateBreadRequest.getName(), updateBreadRequest.getPrice());

            MultipartFile breadImage = breadImageList.get(i);
            if(breadImage != null && !breadImage.isEmpty()) {
                s3Uploader.deleteFileS3(bread.getImage());
                String imagePath = fileConverter.parseFileInfo(breadImage, ImageType.BREAD_IMAGE, bread.getId());
                String image = s3Uploader.upload(breadImage, imagePath);
                bread.updateImage(image);
            }
        }
    }

    @Transactional(readOnly = true)
    public AdminBakeryReviewImageListDto getBakeryReviewImages(Long bakeryId, Pageable pageable) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        return AdminBakeryReviewImageListDto.builder()
                .images(reviewImageRepository.findSliceByBakery(bakery, pageable)).build();
    }

    @Transactional
    public void deleteBakery(Long bakeryId) { // TODO : casacade
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        flagBakeryRepository.deleteByBakery(bakery);
        bakeryDeleteReportRepository.deleteByBakery(bakery);
        bakeryUpdateReportRepository.deleteByBakery(bakery);
        breadAddReportRepository.deleteByBakery(bakery);
        reviewImageRepository.deleteByBakery(bakery);
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
