package com.depromeet.breadmapbackend.restdocs.utils;

import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BreadRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.security.token.RefreshTokenRepository;
import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.review.ReviewService;
import com.depromeet.breadmapbackend.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
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
    protected UserService userService;

    @Autowired
    protected FlagService flagService;

    @Autowired
    protected BakeryService bakeryService;

    @Autowired
    protected ReviewService reviewService;
}
