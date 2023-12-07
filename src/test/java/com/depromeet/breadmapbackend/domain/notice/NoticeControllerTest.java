package com.depromeet.breadmapbackend.domain.notice;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class NoticeControllerTest extends ControllerTest {
	private JwtToken token;

	@BeforeEach
	public void setup() {
		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		User fromUser = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId2").build())
			.userInfo(UserInfo.builder().nickName("nickname2").build())
			.build();
		final User savedUser = userRepository.save(user);
		final User savedFromUser = userRepository.save(fromUser);
		token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

		NoticeToken noticeToken = NoticeToken.builder().user(user).deviceToken("deviceToken").build();
		noticeTokenRepository.save(noticeToken);

		Follow follow = Follow.builder().fromUser(fromUser).toUser(user).build();
		followRepository.save(follow);

		Notice notice1 = Notice.builder()
			.user(savedUser)
			.contentId(1L)
			.title("팔로우 알림")
			.content("%s님이 회원님을 팔로우하기 시작했어요")
			.contentParam("팔달산불나방")
			.type(NoticeType.FOLLOW)
			.extraParam("FOLLOW")
			.build();
		Notice notice2 = Notice.builder()
			.user(savedUser)
			.contentId(savedFromUser.getId())
			.title("좋아요 알림")
			.content("내 리뷰를 %s님이 좋아해요!")
			.contentParam("치악산호랑이")
			.type(NoticeType.REVIEW_LIKE)
			.build();
		Notice notice3 = Notice.builder()
			.user(savedUser)
			.contentId(savedFromUser.getId())
			.title("큐레이션 추가 알림")
			.content("8월 1번째 큐레이션입니닿ㅎㅎㅎㅎ")
			.type(NoticeType.CURATION)
			.build();
		noticeRepository.save(notice1);
		noticeRepository.save(notice2);
		noticeRepository.save(notice3);
	}

	@AfterEach
	public void setDown() {
		noticeTokenRepository.deleteAllInBatch();
		noticeRepository.deleteAllInBatch();
		followRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	void getNoticeList() throws Exception {
		mockMvc.perform(get("/v1/notices?page=0")
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/notice",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
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
					fieldWithPath("data.contents.[].title").description("알람 제목"),
					fieldWithPath("data.contents.[].contentId").description("알람 내용의 고유 번호 : " +
						"(내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 팔로우한 유저 아이디)").optional(),
					fieldWithPath("data.contents.[].subContentId").description("알람 내용의 부모 고유 번호 : " +
						"(댓글달린 게시글 or 리뷰의 id)").optional(),
					fieldWithPath("data.contents.[].content").description("알람 세부 내용 : " +
						"(내가 쓴 리뷰 내용 or 내가 쓴 댓글 내용, 팔로우/팔로잉 알람일 땐 null)").optional(),
					fieldWithPath("data.contents.[].contentParam").description("알람 메시지 생성용 파라미터 ex) user nickName")
						.optional(),
					fieldWithPath("data.contents.[].isFollow").description("알람 팔로우/팔로잉 알람일 때 팔로우 여부"),
					fieldWithPath("data.contents.[].createdAt").description("알람 생성일"),
					fieldWithPath("data.contents.[].extraParam").description("추가 파라미터 개시글 관련 알림일때 해당 게시글의 postTopic")
						.optional(),
					fieldWithPath("data.contents.[].noticeType").description("알람 타입 (" +
						"FOLLOW(\"팔로우\"), \n" +
						"REVIEW_COMMENT(\"리뷰 댓글\"), \n" +
						"REVIEW_LIKE(\"리뷰 좋아요\"), \n" +
						"REVIEW_RECOMMENT(\"리뷰 대댓글\"), \n" +
						"REVIEW_COMMENT_LIKE(\"리뷰 댓글 좋아요\"), \n" +
						"RECOMMENT(\"커뮤니티 대댓글\"), \n" +
						"COMMENT_LIKE(\"커뮤니티 댓글 좋아요\"), \n" +
						"COMMUNITY_LIKE(\"커뮤니티글 좋아요\"), \n" +
						"COMMUNITY_COMMENT(\"커뮤니티 댓글\"), \n" +
						"REPORT_BAKERY_ADDED(\"제보한 빵집 추가\"), \n" +
						"ADD_PRODUCT(\"제보한 빵 추가\"), \n" +
						"EVENT(\"제보한 상품 추가\"), \n" +
						"BAKERY_ADDED(\"빵집 추가\"), \n" +
						"CURATION(\"큐레이션\"), \n"
					)
				)
			))
			.andExpect(status().isOk());
	}
}