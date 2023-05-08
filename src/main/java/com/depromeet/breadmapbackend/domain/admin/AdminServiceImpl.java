package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.admin.dto.AdminImageDto;
import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.domain.image.Image;
import com.depromeet.breadmapbackend.domain.image.ImageRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomJWTKeyProperties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.domain.admin.dto.*;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.global.security.token.RedisTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final BakeryRepository bakeryRepository;
    private final AdminRepository adminRepository;
    private final BakeryAddReportRepository bakeryAddReportRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ImageRepository imageRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenUtils redisTokenUtils;
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

        return createNewToken(admin.getEmail(), admin.getRoleType());
    }

    @Transactional(rollbackFor = Exception.class)
    public JwtToken reissue(ReissueRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken()) ||
                !redisTokenUtils.isRefreshTokenValid(request.getRefreshToken(), request.getAccessToken()) ||
                redisTokenUtils.isBlackList(request.getAccessToken()))
            throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);

        String email = redisTokenUtils.getOAuthIdFromRefreshToken(request.getRefreshToken());
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND));

        JwtToken reissueToken = createNewToken(email, admin.getRoleType());
        makeRefreshTokenInvalid(request.getRefreshToken()); // TODO : accessToken이 유효기간 남아 있으면?
        return reissueToken;
    }

    private JwtToken createNewToken(String email, RoleType roleType) {
        JwtToken jwtToken = jwtTokenProvider.createJwtToken(email, roleType.getCode());
        // key : refreshToken, value : email:accessToken
        redisTokenUtils.setRefreshToken(
                jwtToken.getRefreshToken(),
                email + ":" + jwtToken.getAccessToken(),
                jwtTokenProvider.getRefreshTokenExpiredDate()
        );
        return jwtToken;
    }

    private void makeRefreshTokenInvalid(String refreshToken) {
        redisTokenUtils.deleteRefreshToken(refreshToken);
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
    public AdminImageDto uploadImage(MultipartFile image) throws IOException {
        String hashValue = fileConverter.generateImageHash(image);
        String imagePath = customAWSS3Properties.getDefaultBucket().getImage() + "/" + hashValue + fileConverter.generateImageExtension(image);

        if (imageRepository.findByHashValue(hashValue).isPresent())
            return AdminImageDto.builder().imagePath(s3Uploader.alreadyUpload(imagePath)).build();

        imageRepository.save(Image.builder().hashValue(hashValue).build());
        return AdminImageDto.builder().imagePath(s3Uploader.upload(image, imagePath)).build();
    }
}
