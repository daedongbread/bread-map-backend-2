//package com.depromeet.breadmapbackend.domain.admin.feed.service;
//
//import com.depromeet.breadmapbackend.domain.admin.Admin;
//import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
//import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
//import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
//import com.depromeet.breadmapbackend.domain.admin.feed.repository.LandingFeedRepository;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mockStatic;
//
//@ExtendWith(MockitoExtension.class)
//public class LandingFeedServiceTest {
//
//    @InjectMocks
//    private LandingFeedService landingFeedService;
//
//    @Mock
//    private LandingFeedRepository landingFeedRepository;
//
//    @Mock
//    private AdminRepository adminRepository;
//
//    @Mock
//    private CategoryRepository categoryRepository;
//    private static MockedStatic<FeedAssembler> feedAssembler;
//
//    @BeforeAll
//    public static void beforeALl() {
//        feedAssembler = mockStatic(FeedAssembler.class);
//    }
//
//    @AfterAll
//    public static void afterALl() {
//        feedAssembler.close();
//    }
//
//    @DisplayName("랜딩 피드를 등록한다")
//    @Test
//    void 랜딩_피드_등록_테스트() {
//
//        //given
//        given(adminRepository.findById(any(Long.class)))
//                .willReturn();
//        given(categoryRepository.findById(any(Long.class)))
//                .willReturn();
//        given(FeedAssembler.toEntity())
//                .willReturn();
//
//        //when
//        landingFeedService.addFeed()
//
//        //then
//    }
//}
