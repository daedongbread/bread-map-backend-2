package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeDayType;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeTokenRequest;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends ControllerTest {
    private JwtToken token;

    @BeforeEach
    public void setup() {
        User user = User.builder().nickName("nickname1").roleType(RoleType.USER).username("username1").build();
        User fromUser = User.builder().nickName("nickname2").roleType(RoleType.USER).username("username2").build();
        userRepository.save(user);
        userRepository.save(fromUser);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        NoticeToken noticeToken = NoticeToken.builder().user(user).deviceToken("deviceToken").build();
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
        ResultActions result = mockMvc.perform(post("/v1/notices")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/notice/token/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("deviceToken").description("디바이스 토큰"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getNoticeList() throws Exception {
        mockMvc.perform(get("/v1/notices/{type}?page=0", NoticeDayType.TODAY)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/notice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("type").description("알림 날짜 타입 " +
                                        "(today : 오늘 알림 조회, week :이번주 월요일부터 어제까지의 알림 조회 , before : 저번주 일요일 전의 알림 조회)")),
                        requestParameters(
                                parameterWithName("lastId").optional().description("지난 알림 마지막 고유 번호 (두번째 페이지부터 필요)"),
                                parameterWithName("page").description("현재 페이지 번호 (0부터)")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("알람 리스트"),
                                fieldWithPath("data.contents.[].noticeId").description("알람 아이디"),
                                fieldWithPath("data.contents.[].image").description("알람 이미지").optional(),
                                fieldWithPath("data.contents.[].fromUserId").description("알람 발신 유저 고유 번호"),
                                fieldWithPath("data.contents.[].fromUserNickName").description("알람 발신 유저 닉네임"),
                                fieldWithPath("data.contents.[].title").description("알람 제목"),
                                fieldWithPath("data.contents.[].contentId").description("알람 내용의 고유 번호 : " +
                                        "(내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 팔로우한 유저 아이디)").optional(),
                                fieldWithPath("data.contents.[].content").description("알람 세부 내용 : " +
                                        "(내가 쓴 리뷰 내용 or 내가 쓴 댓글 내용, 팔로우/팔로잉 알람일 땐 null)").optional(),
                                fieldWithPath("data.contents.[].isFollow").description("알람 팔로우/팔로잉 알람일 때 팔로우 여부"),
                                fieldWithPath("data.contents.[].createdAt").description("알람 생성일"),
                                fieldWithPath("data.contents.[].noticeType").description("알람 타입")
                        )
                ))
                .andExpect(status().isOk());
    }
}