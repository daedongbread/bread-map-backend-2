package com.depromeet.breadmapbackend.utils;

import com.depromeet.breadmapbackend.domain.admin.repository.AdminRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportImageRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.repository.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.infra.EmbeddedRedisConfig;
import com.depromeet.breadmapbackend.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import({AmazonS3MockConfig.class, EmbeddedRedisConfig.class})
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected StringRedisTemplate redisTemplate;

    @Autowired
    protected CustomRedisProperties customRedisProperties;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BakeryRepository bakeryRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected ReviewImageRepository reviewImageRepository;

    @Autowired
    protected ReviewProductRatingRepository reviewProductRatingRepository;

    @Autowired
    protected ReviewLikeRepository reviewLikeRepository;

    @Autowired
    protected ReviewCommentRepository reviewCommentRepository;

    @Autowired
    protected ReviewCommentLikeRepository reviewCommentLikeRepository;

    @Autowired
    protected FlagRepository flagRepository;

    @Autowired
    protected FlagBakeryRepository flagBakeryRepository;

    @Autowired
    protected FollowRepository followRepository;

    @Autowired
    protected BakeryUpdateReportRepository bakeryUpdateReportRepository;

    @Autowired
    protected BakeryDeleteReportRepository bakeryDeleteReportRepository;

    @Autowired
    protected BakeryAddReportRepository bakeryAddReportRepository;

    @Autowired
    protected BakeryReportImageRepository bakeryReportImageRepository;

    @Autowired
    protected ProductAddReportRepository productAddReportRepository;

    @Autowired
    protected ProductAddReportImageRepository productAddReportImageRepository;

    @Autowired
    protected ReviewReportRepository reviewReportRepository;

    @Autowired
    protected BlockUserRepository blockUserRepository;

    @Autowired
    protected NoticeRepository noticeRepository;

    @Autowired
    protected NoticeTokenRepository noticeTokenRepository;

    @Autowired
    protected AdminRepository adminRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected FlagService flagService;

    @Autowired
    protected BakeryService bakeryService;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected AdminService adminService;
}
