package com.depromeet.breadmapbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.AdminService;
import com.depromeet.breadmapbackend.domain.auth.AuthService;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.BakeryService;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImageRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportImageRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryReportImageRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryUpdateReportImageRepository;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryUpdateReportRepository;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagService;
import com.depromeet.breadmapbackend.domain.notice.NoticeRepository;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewImageRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRatingRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentRepository;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeRepository;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeRepository;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.review.view.ReviewViewRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.UserService;
import com.depromeet.breadmapbackend.domain.user.block.BlockUserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.security.token.RedisTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import({AmazonS3MockConfig.class })
public abstract class ControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@MockBean
	protected AuthService authService;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected StringRedisTemplate redisTemplate;

	@Autowired
	protected RedisTokenUtils redisTokenUtils;

	@Autowired
	protected CustomRedisProperties customRedisProperties;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected BakeryRepository bakeryRepository;

	@Autowired
	protected BakeryViewRepository bakeryViewRepository;

	@Autowired
	protected ProductRepository productRepository;

	@Autowired
	protected ReviewRepository reviewRepository;

	@Autowired
	protected ReviewViewRepository reviewViewRepository;

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
	protected BakeryUpdateReportImageRepository bakeryUpdateReportImageRepository;

	@Autowired
	protected BakeryAddReportRepository bakeryAddReportRepository;
	@Autowired
	protected BakeryAddReportImageRepository bakeryAddReportImageRepository;

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

	@Autowired
	protected S3Uploader s3Uploader;

	@Autowired
	protected CustomAWSS3Properties customAWSS3Properties;

	@BeforeEach
	void beforeEach() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

}
