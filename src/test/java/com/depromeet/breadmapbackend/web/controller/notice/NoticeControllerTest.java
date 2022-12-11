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
    void getNoticeTokenAlarm() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(
                NoticeTokenRequest.builder().deviceToken(noticeToken.getDeviceToken()).build());

        // when
        ResultActions result = mockMvc.perform(get("/notice/token/alarm")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("notice/token/alarm",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("deviceToken").description("디바이스 토큰")),
                        responseFields(fieldWithPath("data.alarmOn").description("알람 상태"))
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateNoticeTokenAlarm() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(
                NoticeTokenRequest.builder().deviceToken(noticeToken.getDeviceToken()).build());

        // when
        ResultActions result = mockMvc.perform(patch("/notice/token/alarm")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("notice/token/alarm/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("deviceToken").description("디바이스 토큰"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getNoticeList() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(
                NoticeTokenRequest.builder().deviceToken(noticeToken.getDeviceToken()).build());

        // when
        ResultActions result = mockMvc.perform(get("/notice")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("notice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("deviceToken").description("디바이스 토큰")),
                        responseFields(
                                fieldWithPath("data.todayNoticeList").description("오늘 알람 리스트"),
                                fieldWithPath("data.todayNoticeList.[].noticeId").description("오늘 알람 아이디"),
                                fieldWithPath("data.todayNoticeList.[].image").description("오늘 알람 이미지").optional(),
                                fieldWithPath("data.todayNoticeList.[].fromUserId").description("오늘 알람 발신 유저 고유 번호"),
                                fieldWithPath("data.todayNoticeList.[].fromUserNickName").description("오늘 알람 발신 유저 닉네임"),
                                fieldWithPath("data.todayNoticeList.[].title").description("오늘 알람 제목"),
                                fieldWithPath("data.todayNoticeList.[].contentId").description("오늘 알람 내용의 고유 번호 : " +
                                        "(내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디, 팔로우/팔로잉 알람일 땐 null)").optional(),
                                fieldWithPath("data.todayNoticeList.[].content").description("오늘 알람 세부 내용 : " +
                                        "(내가 쓴 리뷰 내용 or 내가 쓴 댓글 내용, 팔로우/팔로잉 알람일 땐 null)").optional(),
                                fieldWithPath("data.todayNoticeList.[].isFollow").description("오늘 알람 팔로우/팔로잉 알람일 때 팔로우 여부"),
                                fieldWithPath("data.todayNoticeList.[].createdAt").description("오늘 알람 생성일"),
                                fieldWithPath("data.todayNoticeList.[].noticeType").description("오늘 알람 타입"),
                                fieldWithPath("data.weekNoticeList").description("이번주 알람 리스트"),
                                fieldWithPath("data.beforeNoticeList").description("지난 알람 리스트")
                        )
                ))
                .andExpect(status().isOk());
    }
}