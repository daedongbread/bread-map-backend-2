package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomJWTKeyProperties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.domain.admin.dto.*;
import com.depromeet.breadmapbackend.domain.user.dto.ReissueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final BakeryRepository bakeryRepository;
    private final AdminRepository adminRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final CustomRedisProperties customRedisProperties;
    private final CustomJWTKeyProperties customJWTKeyProperties;
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
        Integer bakeryAddReportCount = Math.toIntExact(bakeryAddReportRepository.countByStatus(BakeryAddReportStatus.BEFORE_REFLECT));
        Integer bakeryCount = Math.toIntExact(bakeryRepository.countByStatus(BakeryStatus.UNPOSTING));
        Integer reviewReportCount = Math.toIntExact(reviewReportRepository.countByIsBlock(false));
        return AdminBarDto.builder()
                .bakeryAddReportCount(bakeryAddReportCount).bakeryCount(bakeryCount).reviewReportCount(reviewReportCount).build();
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
    public TempImageDto uploadTempImage(MultipartFile file) throws IOException {
        String imagePath = fileConverter.parseFileInfo(file, ImageType.ADMIN_TEMP_IMAGE, 0L);
        String image = s3Uploader.upload(file, imagePath);
        return TempImageDto.builder().image(image).build(); // cloudFrontDomain/adminTempImage/0/image.jpg
    }
}
