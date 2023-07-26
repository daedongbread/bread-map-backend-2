package com.depromeet.breadmapbackend.domain.post;

import static com.depromeet.breadmapbackend.domain.post.PostControllerAssertions.*;
import static com.depromeet.breadmapbackend.domain.post.PostControllerTestSteps.*;
import static com.depromeet.breadmapbackend.domain.post.PostTestData.*;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * PostControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
@DisplayName("Post(커뮤니티) controller 테스트")
class PostControllerTest extends ControllerTest {

	private String 사용자_토큰;
	private String 사용자_토큰2;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
		사용자_토큰2 = jwtTokenProvider.createJwtToken("APPLE_222", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void 빵_이야기_등록() throws Exception {
		// given
		final var 빵_이야기_작성_데이터 = new PostRequest("제목1234567890", "내용12345678910", List.of("image1", "image2"),
			PostTopic.BREAD_STORY);

		// when
		final var 결과 = 빵_이야기_작성_요청(빵_이야기_작성_데이터, 사용자_토큰, mockMvc, objectMapper);

		// then
		빵_이야기_작성_요청_결과_검증(결과);

	}

	@Test
	void 빵_이야기_상세_조회() throws Exception {
		// given
		final var 빵_이야기_고유_번호 = 224;
		final var 커뮤니티_토픽 = PostTopic.EVENT;

		// when
		final var 결과 = 빵_이야기_상세_조회_요청(빵_이야기_고유_번호, 커뮤니티_토픽, 사용자_토큰, mockMvc);

		//then
		빵_이야기_상세_조회_요청_결과_검증(결과);
	}

	@Test
	void 커뮤니티_전체_카테고리_카드_조회() throws Exception {
		// given
		final var 커뮤니티_조회_페이지_데이터 = new CommunityPage(0L, 0L, 0);

		// when
		final var 결과 = 커뮤니티_전체_카테고리_조회_요청(커뮤니티_조회_페이지_데이터, 사용자_토큰, mockMvc);

		// then
		커뮤니티_전체_카테고리_조회_요청_결과_검증(결과);
	}

	@Test
	void 커뮤니티_추천_카드_조회() throws Exception {
		// when
		final var 결과 = 커뮤니티_추천_조회_요청(사용자_토큰, mockMvc);

		// then
		커뮤니티_추천_조회_요청_결과_검증(결과);
	}

	@Test
	void 커뮤니티_글_좋아요() throws Exception {
		// given
		final var 좋아요_대상_커뮤니티글_고유_번호 = 224L;
		// when
		final var 결과 = 커뮤니티_글_좋아요_요청(좋아요_대상_커뮤니티글_고유_번호, 사용자_토큰, mockMvc);

		// then
		커뮤니티_글_좋아요_요청_결과_검증(결과);
	}

	@Test
	void 커뮤니티_글_수정() throws Exception {
		// given
		final var 커뮤니티글_수정_요청_데이터 = new PostRequest(
			"updated title",
			"updated content",
			List.of("updated image1", "updated image2"),
			PostTopic.BREAD_STORY
		);
		final var 수정_대상_커뮤니티_글 = 999L;
		// when
		final var 결과 = 커뮤니티_글_수정_요청(수정_대상_커뮤니티_글, 커뮤니티글_수정_요청_데이터, 사용자_토큰, mockMvc, objectMapper);

		// then
		커뮤니티_글_수정_요청_결과_검증(결과);
	}

	@Test
	void 커뮤니티_글_삭제() throws Exception {
		// given
		final var 삭제_대상_커뮤니티_글 = 222L;
		// when
		final var 결과 = 커뮤니티_글_삭제_요청(삭제_대상_커뮤니티_글, PostTopic.BREAD_STORY, 사용자_토큰2, mockMvc);

		// then
		커뮤니티_글_삭제_요청_결과_검증(결과);
	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-test-data.sql"));
			prepareReviewData(connection);
			prepareReviewCommentData(connection);
			preparePostLikeData(connection);
			preparePostData(connection);
		}
	}

}