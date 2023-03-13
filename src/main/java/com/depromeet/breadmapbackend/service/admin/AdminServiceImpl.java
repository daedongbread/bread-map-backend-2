package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminBakeryImageType;
import com.depromeet.breadmapbackend.domain.admin.repository.AdminRepository;
import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportImageRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewImageRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.repository.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.infra.feign.client.SgisClient;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisTranscoordDto;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisTokenDto;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisGeocodeDto;
import com.depromeet.breadmapbackend.infra.feign.exception.SgisFeignException;
import com.depromeet.breadmapbackend.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.infra.properties.CustomJWTKeyProperties;
import com.depromeet.breadmapbackend.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.infra.properties.CustomSGISKeyProperties;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.admin.dto.SimpleBakeryAddReportDto;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ProductRepository productRepository;
    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final BakeryUpdateReportRepository bakeryUpdateReportRepository;
    private final BakeryReportImageRepository bakeryReportImageRepository;
    private final ProductAddReportRepository productAddReportRepository;
    private final ProductAddReportImageRepository productAddReportImageRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final SgisClient sgisClient;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final CustomRedisProperties customRedisProperties;
    private final CustomJWTKeyProperties customJWTKeyProperties;
    private final CustomSGISKeyProperties customSGISKeyProperties;
    private final CustomAWSS3Properties customAWSS3Properties;

    @Transactional(rollbackFor = Exception.class)
    public void adminJoin(AdminJoinRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent())
            throw new DaedongException(DaedongStatus.ADMIN_EMAIL_DUPLICATE_EXCEPTION);
        if (!request.getSecret().equals(customJWTKeyProperties.getAdmin()))
            throw new DaedongException(DaedongStatus.ADMIN_KEY_EXCEPTION);
        Admin admin = Admin.builder().email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).build();
        adminRepository.save(admin);
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtToken adminLogin(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword()))
            throw new DaedongException(DaedongStatus.USER_NOT_FOUND);

        JwtToken adminToken = jwtTokenProvider.createJwtToken(admin.getEmail(), "ROLE_ADMIN");
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getAdminRefresh() + ":" + admin.getId(),
                        adminToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
        return adminToken;
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtToken reissue(ReissueRequest request) {
        if (!jwtTokenProvider.verifyToken(request.getRefreshToken()))
            throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND));

        String refreshToken = redisTemplate.opsForValue().get(customRedisProperties.getKey().getAdminRefresh() + ":" + admin.getId());
        if (refreshToken == null || !refreshToken.equals(request.getRefreshToken()))
            throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        JwtToken reissueToken = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getAdminRefresh() + ":" + admin.getId(),
                        reissueToken.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        return reissueToken;
    }

    @Transactional(readOnly = true)
    public AdminBarDto getAdminBar() {
        Integer bakeryReportCount = Math.toIntExact(bakeryAddReportRepository.countByStatus(BakeryAddReportStatus.BEFORE_REFLECT));
        Integer bakeryCount = Math.toIntExact(bakeryRepository.countByStatus(BakeryStatus.UNPOSTING));
        Integer reviewReportCount = Math.toIntExact(reviewReportRepository.countByIsBlock(false));
        return AdminBarDto.builder()
                .bakeryReportCount(bakeryReportCount).bakeryCount(bakeryCount).reviewReportCount(reviewReportCount).build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminSimpleBakeryDto> getBakeryList(@RequestParam int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        Page<Bakery> all = bakeryRepository.findPageAll(pageRequest);
        return PageResponseDto.of(all, AdminSimpleBakeryDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public AdminBakeryDto getBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        List<AdminProductDto> productList = productRepository.findByBakery(bakery).stream()
                .filter(Product::isTrue)
                .map(AdminProductDto::new).collect(Collectors.toList());
        return AdminBakeryDto.builder().bakery(bakery).productList(productList).build();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page) {
        PageRequest pageRequest = PageRequest.of(page, 20);
        Page<Bakery> all = bakeryRepository.findByNameContainsOrderByUpdatedAt(name, pageRequest);
        return PageResponseDto.of(all, AdminSimpleBakeryDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryLocationDto getBakeryLatitudeLongitude(String address) {
        SgisTokenDto token = sgisClient.getToken(customSGISKeyProperties.getKey(), customSGISKeyProperties.getSecret());
        SgisGeocodeDto geocode = sgisClient.getGeocode(token.getResult().getAccessToken(), address);
        if (geocode.getResult() == null) throw new SgisFeignException();
        SgisTranscoordDto transcoord = sgisClient.getTranscoord(token.getResult().getAccessToken(),
                5179, 4326, geocode.getResult().getResultdata().get(0).getX(), geocode.getResult().getResultdata().get(0).getY());

        Double latitude = transcoord.getResult().getPosY();
        Double longitude = transcoord.getResult().getPosX();
        return BakeryLocationDto.builder().latitude(latitude).longitude(longitude).build();
    }

    private Long createBakeryId(String address) {
        String bakeryId = null;
        if (address.contains("경기도")) {
            if (address.contains("수원시")) bakeryId = "3740000";
            else if (address.contains("성남시")) bakeryId = "3780000";
            else if (address.contains("의정부시")) bakeryId = "3820000";
            else if (address.contains("안양시")) bakeryId = "3830000";
            else if (address.contains("부천시")) bakeryId = "3860000";
            else if (address.contains("광명시")) bakeryId = "3900000";
            else if (address.contains("평택시")) bakeryId = "3910000";
            else if (address.contains("동두천")) bakeryId = "3920000";
            else if (address.contains("안산시")) bakeryId = "3930000";
            else if (address.contains("고양시")) bakeryId = "3940000";
            else if (address.contains("과천시")) bakeryId = "3970000";
            else if (address.contains("구리시")) bakeryId = "3980000";
            else if (address.contains("남양주")) bakeryId = "3990000";
            else if (address.contains("오산시")) bakeryId = "4000000";
            else if (address.contains("시흥시")) bakeryId = "4010000";
            else if (address.contains("군포시")) bakeryId = "4020000";
            else if (address.contains("의왕시")) bakeryId = "4030000";
            else if (address.contains("하남시")) bakeryId = "4040000";
            else if (address.contains("용인시")) bakeryId = "4050000";
            else if (address.contains("파주시")) bakeryId = "4060000";
            else if (address.contains("이천시")) bakeryId = "4070000";
            else if (address.contains("안성시")) bakeryId = "4080000";
            else if (address.contains("김포시")) bakeryId = "4090000";
            else if (address.contains("연천군")) bakeryId = "4140000";
            else if (address.contains("가평군")) bakeryId = "4160000";
            else if (address.contains("양평군")) bakeryId = "4170000";
            else if (address.contains("화성시")) bakeryId = "5530000";
            else if (address.contains("광주시")) bakeryId = "5540000";
            else if (address.contains("양주시")) bakeryId = "5590000";
            else if (address.contains("포천시")) bakeryId = "5600000";
            else if (address.contains("여주시")) bakeryId = "5700000";
        } else if (address.contains("강원도")) {
            if (address.contains("춘천시")) bakeryId = "4180000";
            else if (address.contains("원주시")) bakeryId = "4190000";
            else if (address.contains("강릉시")) bakeryId = "4200000";
            else if (address.contains("동해시")) bakeryId = "4210000";
            else if (address.contains("태백시")) bakeryId = "4220000";
            else if (address.contains("속초시")) bakeryId = "4230000";
            else if (address.contains("삼척시")) bakeryId = "4240000";
            else if (address.contains("홍천군")) bakeryId = "4250000";
            else if (address.contains("횡성군")) bakeryId = "4260000";
            else if (address.contains("영월군")) bakeryId = "4270000";
            else if (address.contains("평창군")) bakeryId = "4280000";
            else if (address.contains("정선군")) bakeryId = "4290000";
            else if (address.contains("철원군")) bakeryId = "4300000";
            else if (address.contains("화천군")) bakeryId = "4310000";
            else if (address.contains("양구군")) bakeryId = "4320000";
            else if (address.contains("인제군")) bakeryId = "4330000";
            else if (address.contains("고성군")) bakeryId = "4340000";
            else if (address.contains("양양군")) bakeryId = "4350000";
        } else if (address.contains("충청북도")) {
            if (address.contains("충주시")) bakeryId = "4390000";
            else if (address.contains("제천시")) bakeryId = "4400000";
            else if (address.contains("보은군")) bakeryId = "4420000";
            else if (address.contains("옥천군")) bakeryId = "4430000";
            else if (address.contains("영동군")) bakeryId = "4440000";
            else if (address.contains("진천군")) bakeryId = "4450000";
            else if (address.contains("괴산군")) bakeryId = "4460000";
            else if (address.contains("음성군")) bakeryId = "4470000";
            else if (address.contains("단양군")) bakeryId = "4480000";
            else if (address.contains("증평군")) bakeryId = "5570000";
            else if (address.contains("청주시")) bakeryId = "5710000";
        } else if (address.contains("충청남도")) {
            if (address.contains("천안시")) bakeryId = "4490000";
            else if (address.contains("공주시")) bakeryId = "4500000";
            else if (address.contains("보령시")) bakeryId = "4510000";
            else if (address.contains("아산시")) bakeryId = "4520000";
            else if (address.contains("서산시")) bakeryId = "4530000";
            else if (address.contains("논산시")) bakeryId = "4540000";
            else if (address.contains("금산시")) bakeryId = "4550000";
            else if (address.contains("연기군")) bakeryId = "4560000";
            else if (address.contains("부여군")) bakeryId = "4570000";
            else if (address.contains("서천군")) bakeryId = "4580000";
            else if (address.contains("청양군")) bakeryId = "4590000";
            else if (address.contains("홍성군")) bakeryId = "4600000";
            else if (address.contains("예산군")) bakeryId = "4610000";
            else if (address.contains("태안군")) bakeryId = "4620000";
            else if (address.contains("계룡시")) bakeryId = "5580000";
            else if (address.contains("당진시")) bakeryId = "5680000";
        } else if (address.contains("전라북도")) {
            if (address.contains("전주시")) bakeryId = "4640000";
            else if (address.contains("군산시")) bakeryId = "4670000";
            else if (address.contains("익산시")) bakeryId = "4680000";
            else if (address.contains("정읍시")) bakeryId = "4690000";
            else if (address.contains("남원시")) bakeryId = "4700000";
            else if (address.contains("김제시")) bakeryId = "4710000";
            else if (address.contains("완주군")) bakeryId = "4720000";
            else if (address.contains("진안군")) bakeryId = "4730000";
            else if (address.contains("무주군")) bakeryId = "4740000";
            else if (address.contains("장수군")) bakeryId = "4750000";
            else if (address.contains("임실군")) bakeryId = "4760000";
            else if (address.contains("순창군")) bakeryId = "4770000";
            else if (address.contains("고창군")) bakeryId = "4780000";
            else if (address.contains("부안군")) bakeryId = "4790000";
        } else if (address.contains("전라남도")) {
            if (address.contains("목포시")) bakeryId = "4800000";
            else if (address.contains("여수시")) bakeryId = "4810000";
            else if (address.contains("순천시")) bakeryId = "4820000";
            else if (address.contains("나주시")) bakeryId = "4830000";
            else if (address.contains("광양시")) bakeryId = "4840000";
            else if (address.contains("담양군")) bakeryId = "4850000";
            else if (address.contains("곡성군")) bakeryId = "4860000";
            else if (address.contains("구례군")) bakeryId = "4870000";
            else if (address.contains("고흥군")) bakeryId = "4880000";
            else if (address.contains("보성군")) bakeryId = "4890000";
            else if (address.contains("화순군")) bakeryId = "4900000";
            else if (address.contains("장흥군")) bakeryId = "4910000";
            else if (address.contains("강진군")) bakeryId = "4920000";
            else if (address.contains("해남군")) bakeryId = "4930000";
            else if (address.contains("영암군")) bakeryId = "4940000";
            else if (address.contains("무안군")) bakeryId = "4950000";
            else if (address.contains("함평군")) bakeryId = "4960000";
            else if (address.contains("영광군")) bakeryId = "4970000";
            else if (address.contains("장성군")) bakeryId = "4980000";
            else if (address.contains("완도군")) bakeryId = "4990000";
            else if (address.contains("진도군")) bakeryId = "5000000";
            else if (address.contains("신안군")) bakeryId = "5010000";
        } else if (address.contains("경상북도")) {
            if (address.contains("포항시")) bakeryId = "5020000";
            else if (address.contains("경주시")) bakeryId = "5050000";
            else if (address.contains("김천시")) bakeryId = "5060000";
            else if (address.contains("안동시")) bakeryId = "5070000";
            else if (address.contains("구미시")) bakeryId = "5080000";
            else if (address.contains("영주시")) bakeryId = "5090000";
            else if (address.contains("영천시")) bakeryId = "5100000";
            else if (address.contains("상주시")) bakeryId = "5110000";
            else if (address.contains("문경시")) bakeryId = "5120000";
            else if (address.contains("경산시")) bakeryId = "5130000";
            else if (address.contains("군위군")) bakeryId = "5140000";
            else if (address.contains("의성군")) bakeryId = "5150000";
            else if (address.contains("청송군")) bakeryId = "5160000";
            else if (address.contains("영양군")) bakeryId = "5170000";
            else if (address.contains("영덕군")) bakeryId = "5180000";
            else if (address.contains("청도군")) bakeryId = "5190000";
            else if (address.contains("고령군")) bakeryId = "5200000";
            else if (address.contains("성주군")) bakeryId = "5210000";
            else if (address.contains("칠곡군")) bakeryId = "5220000";
            else if (address.contains("예천군")) bakeryId = "5230000";
            else if (address.contains("봉화군")) bakeryId = "5240000";
            else if (address.contains("울진군")) bakeryId = "5250000";
            else if (address.contains("울릉군")) bakeryId = "5260000";
        } else if (address.contains("경상남도")) {
            if (address.contains("진주시")) bakeryId = "5310000";
            else if (address.contains("통영시")) bakeryId = "5330000";
            else if (address.contains("사천시")) bakeryId = "5340000";
            else if (address.contains("김해시")) bakeryId = "5350000";
            else if (address.contains("밀양시")) bakeryId = "5360000";
            else if (address.contains("거제시")) bakeryId = "5370000";
            else if (address.contains("양산시")) bakeryId = "5380000";
            else if (address.contains("의령군")) bakeryId = "5390000";
            else if (address.contains("함안군")) bakeryId = "5400000";
            else if (address.contains("창녕군")) bakeryId = "5410000";
            else if (address.contains("고성군")) bakeryId = "5420000";
            else if (address.contains("남해군")) bakeryId = "5430000";
            else if (address.contains("하동군")) bakeryId = "5440000";
            else if (address.contains("산청군")) bakeryId = "5450000";
            else if (address.contains("함양군")) bakeryId = "5460000";
            else if (address.contains("거창군")) bakeryId = "5470000";
            else if (address.contains("합천군")) bakeryId = "5480000";
            else if (address.contains("창원시")) bakeryId = "5670000";
        } else if (address.contains("제주특별자치도") || address.contains("제주도")) {
            if (address.contains("제주시")) bakeryId = "6510000";
            else if (address.contains("서귀포시")) bakeryId = "6520000";
        } else if (address.contains("서울특별시") || address.contains("서울시")) {
            if (address.contains("종로구")) bakeryId = "3000000";
            else if (address.contains("중구")) bakeryId = "3010000";
            else if (address.contains("용산구")) bakeryId = "3020000";
            else if (address.contains("성동구")) bakeryId = "3030000";
            else if (address.contains("광진구")) bakeryId = "3040000";
            else if (address.contains("동대문구")) bakeryId = "3050000";
            else if (address.contains("중랑구")) bakeryId = "3060000";
            else if (address.contains("성북구")) bakeryId = "3070000";
            else if (address.contains("강북구")) bakeryId = "3080000";
            else if (address.contains("도봉구")) bakeryId = "3090000";
            else if (address.contains("노원구")) bakeryId = "3100000";
            else if (address.contains("은평구")) bakeryId = "3110000";
            else if (address.contains("서대문구")) bakeryId = "3120000";
            else if (address.contains("마포구")) bakeryId = "3130000";
            else if (address.contains("양천구")) bakeryId = "3140000";
            else if (address.contains("강서구")) bakeryId = "3150000";
            else if (address.contains("구로구")) bakeryId = "3160000";
            else if (address.contains("금천구")) bakeryId = "3170000";
            else if (address.contains("영등포구")) bakeryId = "3180000";
            else if (address.contains("동작구")) bakeryId = "3190000";
            else if (address.contains("관악구")) bakeryId = "3200000";
            else if (address.contains("서초구")) bakeryId = "3210000";
            else if (address.contains("강남구")) bakeryId = "3220000";
            else if (address.contains("송파구")) bakeryId = "3230000";
            else if (address.contains("강동구")) bakeryId = "3240000";
        } else if (address.contains("부산광역시") || address.contains("부산시")) {
            if (address.contains("중구")) bakeryId = "3250000";
            else if (address.contains("서구")) bakeryId = "3260000";
            else if (address.contains("동구")) bakeryId = "3270000";
            else if (address.contains("영도구")) bakeryId = "3280000";
            else if (address.contains("부산진구")) bakeryId = "3290000";
            else if (address.contains("동래구")) bakeryId = "3300000";
            else if (address.contains("남구")) bakeryId = "3310000";
            else if (address.contains("북구")) bakeryId = "3320000";
            else if (address.contains("해운대구")) bakeryId = "3330000";
            else if (address.contains("사하구")) bakeryId = "3340000";
            else if (address.contains("금정구")) bakeryId = "3350000";
            else if (address.contains("강서구")) bakeryId = "3360000";
            else if (address.contains("연제구")) bakeryId = "3370000";
            else if (address.contains("수영구")) bakeryId = "3380000";
            else if (address.contains("사상구")) bakeryId = "3390000";
            else if (address.contains("기장군")) bakeryId = "3400000";
        } else if (address.contains("대구광역시") || address.contains("대구시")) {
            if (address.contains("중구")) bakeryId = "3410000";
            else if (address.contains("동구")) bakeryId = "3420000";
            else if (address.contains("서구")) bakeryId = "3430000";
            else if (address.contains("남구")) bakeryId = "3440000";
            else if (address.contains("북구")) bakeryId = "3450000";
            else if (address.contains("수성구")) bakeryId = "3460000";
            else if (address.contains("달서구")) bakeryId = "3470000";
            else if (address.contains("달성군")) bakeryId = "3480000";

        } else if (address.contains("인천광역시") || address.contains("인천시")) {
            if (address.contains("중구")) bakeryId = "3490000";
            else if (address.contains("동구")) bakeryId = "3500000";
            else if (address.contains("미추홀구")) bakeryId = "3510500";
            else if (address.contains("연수구")) bakeryId = "3520000";
            else if (address.contains("남동구")) bakeryId = "3530000";
            else if (address.contains("부평구")) bakeryId = "3540000";
            else if (address.contains("계양구")) bakeryId = "3550000";
            else if (address.contains("서구")) bakeryId = "3560000";
            else if (address.contains("강화군")) bakeryId = "3570000";
            else if (address.contains("옹진군")) bakeryId = "3580000";

        } else if (address.contains("광주광역시") || address.contains("광주시")) {
            if (address.contains("동구")) bakeryId = "359";
            else if (address.contains("서구")) bakeryId = "3600000";
            else if (address.contains("남구")) bakeryId = "3610000";
            else if (address.contains("북구")) bakeryId = "3620000";
            else if (address.contains("광산구")) bakeryId = "3630000";
        } else if (address.contains("대전광역시") || address.contains("대전시")) {
            if (address.contains("동구")) bakeryId = "3640000";
            else if (address.contains("중구")) bakeryId = "3650000";
            else if (address.contains("서구")) bakeryId = "3660000";
            else if (address.contains("유성구")) bakeryId = "3670000";
            else if (address.contains("대덕구")) bakeryId = "3680000";

        } else if (address.contains("울산광역시") || address.contains("울산시")) {
            if (address.contains("중구")) bakeryId = "3690000";
            else if (address.contains("남구")) bakeryId = "3700000";
            else if (address.contains("동구")) bakeryId = "3710000";
            else if (address.contains("북구")) bakeryId = "3720000";
            else if (address.contains("울주군")) bakeryId = "3730000";

        } else if (address.contains("세종특별자치시") || address.contains("세종시")) {
            bakeryId = "5690000";
        } else bakeryId = "9999999";

        Random rand = new Random();
        do {
            String random = "";
            for (int i = 0; i < 5; i++) {
                random += Integer.toString(rand.nextInt(9));
            }
            // 자치단체코드 7자리, 년도 뒷 2자리, 번호 5자리 -> 14자리
            String year = String.valueOf(LocalDateTime.now().getYear());
            bakeryId = bakeryId + year.substring(2) + random;
        } while (bakeryRepository.findById(Long.parseLong(bakeryId)).isPresent());
        return Long.valueOf(bakeryId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBakery(BakeryAddRequest request) {
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

        if (request.getImage() != null) {
            String oldImage = request.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String newImage = ImageType.BAKERY_IMAGE.getCode() + "/" + bakeryId + "/" + oldImage.split("/")[oldImage.split("/").length - 1];
            s3Uploader.copy(oldImage, newImage);
            if (oldImage.contains(ImageType.ADMIN_TEMP_IMAGE.getCode()) || oldImage.contains(ImageType.BAKERY_IMAGE.getCode()))
                s3Uploader.deleteFileS3(oldImage);
            bakeryRepository.findById(bakery.getId()).get().updateImage(customAWSS3Properties.getCloudFront() + "/" + newImage); // TODO : ID 직접 할당은 영속성 컨텍스트에서 관리 안되는 것 때문에
        }

        if (request.getProductList() != null && !request.getProductList().isEmpty()) { // TODO
            for (BakeryAddRequest.ProductAddRequest productAddRequest : request.getProductList()) {
                Product product = Product.builder().bakery(bakery).productType(productAddRequest.getProductType())
                        .name(productAddRequest.getProductName()).price(productAddRequest.getPrice()).build();
                productRepository.save(product);

                if (productAddRequest.getImage() != null) {
                    String oldImage = request.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
                    String newImage = ImageType.PRODUCT_IMAGE.getCode() + "/" + product.getId() + "/" + oldImage.split("/")[oldImage.split("/").length - 1];
                    s3Uploader.copy(oldImage, newImage);
                    if (oldImage.contains(ImageType.ADMIN_TEMP_IMAGE.getCode()) || oldImage.contains(ImageType.PRODUCT_IMAGE.getCode()))
                        s3Uploader.deleteFileS3(oldImage);
                    product.updateImage(customAWSS3Properties.getCloudFront() + "/" + newImage);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBakery(Long bakeryId, BakeryUpdateRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        bakery.update(bakeryId, request.getName(),
                request.getAddress(), request.getLatitude(), request.getLongitude(), request.getHours(),
                request.getWebsiteURL(), request.getInstagramURL(), request.getFacebookURL(), request.getBlogURL(),
                request.getPhoneNumber(), request.getFacilityInfoList(), request.getStatus());

        if (request.getImage() != null && !request.getImage().equals(bakery.getImage())) {
            String oldImage = request.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String newImage = ImageType.BAKERY_IMAGE.getCode() + "/" + bakeryId + "/" + oldImage.split("/")[oldImage.split("/").length - 1];
            s3Uploader.copy(oldImage, newImage);
            if (oldImage.contains(ImageType.ADMIN_TEMP_IMAGE.getCode()) || oldImage.contains(ImageType.BAKERY_IMAGE.getCode()))
                s3Uploader.deleteFileS3(oldImage);
            bakery.updateImage(customAWSS3Properties.getCloudFront() + "/" + newImage);
        }

        if (request.getProductList() != null && !request.getProductList().isEmpty()) { // TODO
            for (BakeryUpdateRequest.ProductUpdateRequest productUpdateRequest : request.getProductList()) {
                Product product;
                if (productUpdateRequest.getProductId() == null) { // 새로운 product 일 때
                    product = Product.builder()
                            .productType(productUpdateRequest.getProductType()).bakery(bakery)
                            .name(productUpdateRequest.getProductName()).price(productUpdateRequest.getPrice()).build();
                    productRepository.save(product);

                    if (productUpdateRequest.getImage() != null) {
                        String oldImage = request.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
                        String newImage = ImageType.PRODUCT_IMAGE.getCode() + "/" + product.getId() + "/" + oldImage.split("/")[oldImage.split("/").length - 1];
                        s3Uploader.copy(oldImage, newImage);
                        if (oldImage.contains(ImageType.ADMIN_TEMP_IMAGE.getCode()) || oldImage.contains(ImageType.PRODUCT_IMAGE.getCode()))
                            s3Uploader.deleteFileS3(oldImage);
                        product.updateImage(customAWSS3Properties.getCloudFront() + "/" + newImage);
                    }
                } else { // 기존 product 일 때
                    product = productRepository.findById(productUpdateRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                    product.update(productUpdateRequest.getProductType(), productUpdateRequest.getProductName(), productUpdateRequest.getPrice());

                    if (productUpdateRequest.getImage() != null && !productUpdateRequest.getImage().equals(product.getImage())) {
                        String oldImage = request.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
                        String newImage = ImageType.PRODUCT_IMAGE.getCode() + "/" + product.getId() + "/" + oldImage.split("/")[oldImage.split("/").length - 1];
                        s3Uploader.copy(oldImage, newImage);
                        if (oldImage.contains(ImageType.ADMIN_TEMP_IMAGE.getCode()) || oldImage.contains(ImageType.PRODUCT_IMAGE.getCode()))
                            s3Uploader.deleteFileS3(oldImage);
                        product.updateImage(customAWSS3Properties.getCloudFront() + "/" + newImage);
                    }
                }

            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long bakeryId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
        s3Uploader.deleteFileS3(product.getImage());
        reviewProductRatingRepository.deleteByProductId(productId);
        productRepository.delete(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType type, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        PageRequest pageable = PageRequest.of(page, 16, Sort.by("createdAt").descending());

        PageResponseDto<AdminImageDto> adminImages;
        if (type.equals(AdminBakeryImageType.bakeryReportImage)) {
            Page<BakeryReportImage> bakeryReportImages = bakeryReportImageRepository.findPageByBakery(bakery, pageable);
            adminImages = PageResponseDto.of(bakeryReportImages, AdminImageDto::new);
            bakeryReportImages.forEach(BakeryReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.productAddReportImage)) {
            Page<ProductAddReportImage> productAddReportImages = productAddReportImageRepository.findPageByBakeryAndIsRegisteredIsTrue(bakery, pageable);
            adminImages = PageResponseDto.of(productAddReportImages, AdminImageDto::new);
            productAddReportImages.forEach(ProductAddReportImage::unNew);
        } else if (type.equals(AdminBakeryImageType.reviewImage)) {
            Page<ReviewImage> reviewImages = reviewImageRepository.findPageByBakeryAndIsHideIsFalse(bakery, pageable);
            adminImages = PageResponseDto.of(reviewImages, AdminImageDto::new);
            reviewImages.forEach(ReviewImage::unNew);
        } else throw new DaedongException(DaedongStatus.ADMIN_IMAGE_TYPE_EXCEPTION);
        return adminImages;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadAdminImage(String image) throws IOException {
        byte[] bytes = s3Uploader.getObject(image);
        String fileName = URLEncoder.encode(image.replace(customAWSS3Properties.getCloudFront() + "/", ""), "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        if (type.equals(AdminBakeryImageType.bakeryReportImage)) {
            BakeryReportImage bakeryReportImage = bakeryReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_IMAGE_REPORT_NOT_FOUND));
            String replace = bakeryReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                bakeryReportImageRepository.delete(bakeryReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.productAddReportImage)) {
            ProductAddReportImage productAddReportImage = productAddReportImageRepository.findById(imageId)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND));

            String replace = productAddReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else {
                productAddReportImageRepository.delete(productAddReportImage);
                s3Uploader.deleteFileS3(replace);
            }
        } else if (type.equals(AdminBakeryImageType.reviewImage)) {
            ReviewImage reviewImage = reviewImageRepository.findByIdAndBakery(imageId, bakery)
                    .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_IMAGE_NOT_FOUND));

            String replace = reviewImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            else reviewImage.hide();
        } else throw new DaedongException(DaedongStatus.ADMIN_IMAGE_TYPE_EXCEPTION);
    }

    private Boolean isUsedImage(Bakery bakery, String image) {
        Set<String> usedImage = bakery.getProductList().stream()
                .filter(product -> product.getImage() != null)
                .map(product -> {
                    String replace = product.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
                    return replace.split("/")[replace.split("/").length - 1];
        }).collect(Collectors.toSet());

        if (bakery.getImage() != null) {
            String replace = bakery.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            usedImage.add(replace.split("/")[replace.split("/").length - 1]);
        }
        return usedImage.contains(image);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<ProductAddReport> contents = productAddReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        return PageResponseDto.of(contents, ProductAddReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void registerProductAddImage(Long bakeryId, Long reportId, ProductAddImageRegisterRequest request) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        ProductAddReport productAddReport = productAddReportRepository.findByIdAndBakery(reportId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_NOT_FOUND));

        request.getImageIdList().forEach(
                id -> {
                    ProductAddReportImage productAddReportImage = productAddReportImageRepository.findByIdAndProductAddReport(id, productAddReport)
                            .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_IMAGE_NOT_FOUND));
                    productAddReportImage.register();
                }
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProductAddReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        ProductAddReport productAddReport = productAddReportRepository.findByIdAndBakery(reportId, bakery)
                .orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_ADD_REPORT_NOT_FOUND));
        productAddReport.getImages().forEach(productAddReportImage -> {
            String replace = productAddReportImage.getImage().replace(customAWSS3Properties.getCloudFront() + "/", "");
            String image = replace.split("/")[replace.split("/").length - 1];

            if (isUsedImage(bakery, image)) throw new DaedongException(DaedongStatus.ADMIN_IMAGE_UNDELETE_EXCEPTION);
            s3Uploader.deleteFileS3(replace);
        });
        productAddReportRepository.delete(productAddReport);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<BakeryUpdateReport> contents = bakeryUpdateReportRepository.findPageByBakery(bakery, pageable); // TODO N+1
        return PageResponseDto.of(contents, BakeryUpdateReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeBakeryUpdateReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        BakeryUpdateReport bakeryUpdateReport = bakeryUpdateReportRepository.findByIdAndBakery(reportId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryUpdateReport.change();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBakeryUpdateReport(Long bakeryId, Long reportId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        bakeryUpdateReportRepository.deleteByIdAndBakery(reportId, bakery);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBakery(Long bakeryId) { // TODO : casacade
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        flagBakeryRepository.deleteByBakery(bakery);
        bakeryUpdateReportRepository.deleteByBakery(bakery);
        bakeryReportImageRepository.deleteByBakery(bakery);
        productAddReportRepository.deleteByBakery(bakery);
        reviewImageRepository.deleteByBakery(bakery);
        reviewProductRatingRepository.deleteByBakeryId(bakeryId);
        reviewRepository.findByBakery(bakery).forEach(reviewReportRepository::deleteByReview);
        bakeryRepository.deleteById(bakeryId);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<SimpleBakeryAddReportDto> getBakeryAddReportList(Pageable pageable) {
        Page<BakeryAddReport> all = bakeryAddReportRepository.findPageAll(pageable);
        return PageResponseDto.of(all, SimpleBakeryAddReportDto::new);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BakeryAddReportDto getBakeryAddReport(Long reportId) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_REPORT_NOT_FOUND));
        return BakeryAddReportDto.builder().bakeryAddReport(bakeryAddReport).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBakeryAddReportStatus(Long reportId, BakeryReportStatusUpdateRequest request) {
        BakeryAddReport bakeryAddReport = bakeryAddReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_REPORT_NOT_FOUND));
        bakeryAddReport.updateStatus(request.getStatus());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminReviewReportDto> getReviewReportList(Pageable pageable) {
        Page<ReviewReport> all = reviewReportRepository.findPageAll(pageable);
        return PageResponseDto.of(all, AdminReviewReportDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateReviewStatus(Long reportId) {
        ReviewReport reviewReport = reviewReportRepository.findById(reportId).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_REPORT_NOT_FOUND));
        reviewReport.getReview().useChange();
        reviewReport.changeBlock();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminUserDto> getUserList(Pageable pageable) {
        Page<User> all = userRepository.findPageAll(pageable);
        return PageResponseDto.of(all, AdminUserDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeUserBlock(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        user.changeBlock();
    }

    @Transactional(rollbackFor = Exception.class)
    public TempImageDto uploadTempImage(MultipartFile file) throws IOException {
        String imagePath = fileConverter.parseFileInfo(file, ImageType.ADMIN_TEMP_IMAGE, 0L);
        String image = s3Uploader.upload(file, imagePath);
        return TempImageDto.builder().image(image).build(); // cloudFrontDomain/adminTempImage/0/image.jpg
    }
}
