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

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void 빵_이야기_등록() throws Exception {
		// given
		final var 빵_이야기_작성_데이터 = new PostRequest("제목", "내용", List.of("image1", "image2"));

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

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-test-data.sql"));
			prepareReviewData(connection);
			prepareReviewCommentData(connection);
			preparePostLikeData(connection);
		}
	}

}