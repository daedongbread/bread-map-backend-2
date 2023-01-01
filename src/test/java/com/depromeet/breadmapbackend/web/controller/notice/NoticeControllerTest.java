package com.depromeet.breadmapbackend.web.controller.notice;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.UpdateNoticeTokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends ControllerTest {
    private User user;
    private User fromUser;
    private JwtToken token;
    private NoticeToken noticeToken;

    @BeforeEach
    public void setup() {
        user = User.builder().nickName("nickname1").roleType(RoleType.USER).username("username1").build();
        fromUser = User.builder().nickName("nickname2").roleType(RoleType.USER).username("username2").build();
        userRepository.save(user);
        userRepository.save(fromUser);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        noticeToken = NoticeToken.builder().user(user).deviceToken("deviceToken").build();
        noticeTokenRepository.save(noticeToken);

        Follow follow = Follow.builder().fromUser(fromUser).toUser(user).build();
        followRepository.save(follow);

        Notice notice1 = Notice.builder().user(user).fromUser(fromUser).
                title("title1").contentId(1L).content("content1").type(NoticeType.REVIEW_COMMENT).build();
        Notice notice2 = Notice.builder().user(user).fromUser(fromUser).
                title("title2").type(NoticeType.FOLLOW).build();
        noticeRepository.save(notice1);
        noticeRepository.save(notice2);
    }

    @AfterEach
    public void setDown() {
        noticeTokenRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void addNoticeToken() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(
                NoticeTokenRequest.builder().deviceToken("newDeviceToken").build());

        // when
        ResultActions result = mockMvc.perform(post("/notice/token")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("notice/token/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("deviceToken").description("디바이스 토큰"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTodayNoticeList() throws Exception {
        mockMvc.perform(get("/notice/today?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("notice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("오늘 알람 리스트"),
                                fieldWithPath("data.contents").description("오늘 알람 리스트"),
                                fieldWithPath("data.contents.[].noticeId").description("오늘 알람 아이디"),
                                fieldWithPath("data.contents.[].image").description("오늘 알람 이미지").optional(),
                                fieldWithPath("data.contents.[].fromUserId").description("오늘 알람 발신 유저 고유 번호"),
                                fieldWithPath("data.contents.[].fromUserNickName").description("오늘 알람 발신 유저 닉네임"),
                                fieldWithPath("data.contents.[].title").description("오늘 알람 제목"),
                                fieldWithPath("data.contents.[].contentId").description("오늘 알람 내용의 고유 번호 : " +
                                        "(내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 팔로우한 유저 아이디)").optional(),
                                fieldWithPath("data.contents.[].content").description("오늘 알람 세부 내용 : " +
                                        "(내가 쓴 리뷰 내용 or 내가 쓴 댓글 내용, 팔로우/팔로잉 알람일 땐 null)").optional(),
                                fieldWithPath("data.contents.[].isFollow").description("오늘 알람 팔로우/팔로잉 알람일 때 팔로우 여부"),
                                fieldWithPath("data.contents.[].createdAt").description("오늘 알람 생성일"),
                                fieldWithPath("data.contents.[].noticeType").description("오늘 알람 타입")
                        )
                ))
                .andExpect(status().isOk());
    }
}