package com.depromeet.breadmapbackend.domain.auth;

import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.depromeet.breadmapbackend.domain.flag.FlagRepository;
import com.depromeet.breadmapbackend.domain.notice.FcmService;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.infra.AsyncConfig;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;
import com.depromeet.breadmapbackend.global.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.global.security.token.OIDCVerifyProcessorImpl;
import com.depromeet.breadmapbackend.global.security.token.RedisTokenUtils;
import com.depromeet.breadmapbackend.global.security.token.verifier.AppleOIDCVerifier;
import com.depromeet.breadmapbackend.global.security.token.verifier.GoogleOIDCVerifier;
import com.depromeet.breadmapbackend.global.security.token.verifier.KakaoOIDCVerifier;
import com.depromeet.breadmapbackend.utils.ServiceTest;
import com.depromeet.breadmapbackend.utils.TestConfig;

/**
 * AuthServiceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/23
 */

@Import({
	AuthServiceImpl.class,
	FcmService.class,
	TestConfig.class,
	AsyncConfig.class,
	UserRepository.class,
	NoticeTokenRepository.class,
	FlagRepository.class,
	RedisTokenUtils.class,
	JwtTokenProvider.class,
	CustomAWSS3Properties.class,
	OIDCVerifyProcessorImpl.class,
	GoogleOIDCVerifier.class,
	AppleOIDCVerifier.class,
	KakaoOIDCVerifier.class,
	StringRedisTemplate.class,
	LettuceConnectionFactory.class
})
public abstract class AuthServiceTest extends ServiceTest {
}
