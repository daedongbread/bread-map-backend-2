package com.depromeet.breadmapbackend.utils;

<<<<<<< HEAD:src/test/java/com/depromeet/breadmapbackend/utils/ControllerTest.java
import com.depromeet.breadmapbackend.domain.bakery.repository.*;
=======
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.bkreport.BkReportRepository;
>>>>>>> 739d5b2 (Delete Add Bakery Report files):src/test/java/com/depromeet/breadmapbackend/restdocs/utils/ControllerTest.java
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.security.token.RefreshTokenRepository;
import com.depromeet.breadmapbackend.service.admin.AdminService;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.service.bkreport.BkReportService;
import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import({AmazonS3MockConfig.class})
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected BakeryRepository bakeryRepository;

    @Autowired
    protected BreadRepository breadRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected BreadRatingRepository breadRatingRepository;

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
    protected BreadAddReportRepository breadAddReportRepository;

    @Autowired
    protected ReviewReportRepository reviewReportRepository;

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
