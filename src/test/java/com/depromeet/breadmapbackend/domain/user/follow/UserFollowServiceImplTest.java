package com.depromeet.breadmapbackend.domain.user.follow;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.global.exception.DaedongException;

/**
 * UserFollowServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/22
 */
class UserFollowServiceImplTest extends UserFollowServiceTest {

	@Autowired
	private UserFollowServiceImpl sut;

	@Test
	@Sql("classpath:user-follow-test-data.sql")
	void 탈퇴한_회원_팔로우시_기대하는_응답을_반환한다() throws Exception {
		//given
		final String followerOauthId = "APPLE_111";
		final Long followeeId = 112L;

		//when
		assertThatThrownBy(() -> sut.follow(followerOauthId, new FollowRequest(followeeId)))
			.isInstanceOf(DaedongException.class)
			.hasFieldOrPropertyWithValue("daedongStatus.code", 40111);

	}

}