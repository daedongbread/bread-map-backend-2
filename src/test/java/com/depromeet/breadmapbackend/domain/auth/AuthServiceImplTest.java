package com.depromeet.breadmapbackend.domain.auth;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;

/**
 * AuthServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/23
 */

@SpringBootTest
class AuthServiceImplTest {

	@Autowired
	private AuthServiceImpl sut;

	@Autowired
	private EntityManager em;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Test
	@Sql("classpath:user-follow-test-data.sql")
	void 회원탈퇴() throws Exception {
		//given
		final Long userId = 111L;
		final User beforeDeregister = em.find(User.class, userId);
		final UserInfo userInfo = beforeDeregister.getUserInfo();
		final OAuthInfo oAuthInfo = beforeDeregister.getOAuthInfo();
		final JwtToken jwtToken = tokenProvider.createJwtToken(beforeDeregister.getOAuthId(), RoleType.USER.getCode());

		//when
		sut.deRegisterUser(new LogoutRequest(jwtToken.getAccessToken(), jwtToken.getRefreshToken(), "device-token"),
			userId);

		//then
		final User deRegisteredUser = em.find(User.class, userId);
		assertThat(deRegisteredUser.isDeRegistered()).isTrue();
		assertThat(deRegisteredUser.getUserInfo().getEmail()).isNotEqualTo(userInfo.getEmail());
		assertThat(deRegisteredUser.getUserInfo().getNickName()).isNotEqualTo(userInfo.getNickName());
		assertThat(deRegisteredUser.getOAuthInfo().getOAuthId()).isNotEqualTo(oAuthInfo.getOAuthId());

	}
}