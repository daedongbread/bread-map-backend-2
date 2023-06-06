package com.depromeet.breadmapbackend.global.security.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.UserService;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.infra.properties.CustomJWTKeyProperties;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final UserRepository userRepository;
	private final UserService userService;
	private final AdminRepository adminRepository;
	private final RedisTokenUtils redisTokenUtils;
	private final CustomJWTKeyProperties customJWTKeyProperties;

	private final Long accessTokenExpiredDate = 1 * 60 * 60 * 1000L; // 1 hours
	private final Long refreshTokenExpiredDate = 14 * 24 * 60 * 60 * 1000L; // 14 days

	private static final String ROLES = "roles";
	private static final String TYPE = "type";

	//    @PostConstruct
	//    protected void init() {
	//        this.key = Base64.getEncoder()
	//                .encodeToString(key.getBytes(StandardCharsets.UTF_8));
	//    }

	private Key getSigningKey() {
		byte[] keyBytes = customJWTKeyProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public JwtToken createJwtToken(String oAuthId, String role) {
		Long id = null;
		if (role.equals(RoleType.USER.getCode())) {
			id = userRepository.findByOAuthId(oAuthId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND))
				.getId();
		} else if (role.equals(RoleType.ADMIN.getCode())) {
			id = adminRepository.findByEmail(oAuthId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND))
				.getId();
		}

		Claims claims = Jwts.claims().setSubject(oAuthId);
		claims.put(ROLES, role);

		Date now = new Date();
		String accessToken = Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredDate))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();

		String refreshToken = Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredDate))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();

		return JwtToken.builder().userId(id)
			.accessToken(accessToken).refreshToken(refreshToken)
			.accessTokenExpiredDate(accessTokenExpiredDate).build();
	}

	public JwtToken createTestJwtToken(String oAuthId, String role) {
		Long id = adminRepository.findByEmail(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND))
			.getId();

		Claims claims = Jwts.claims().setSubject(oAuthId);
		claims.put(ROLES, role);

		Date now = new Date();
		String accessToken = Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(System.currentTimeMillis() + 60 * 1000L))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();

		String refreshToken = Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredDate))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();

		return JwtToken.builder().userId(id)
			.accessToken(accessToken).refreshToken(refreshToken)
			.accessTokenExpiredDate(accessTokenExpiredDate).build();
	}

	// 만료된 토큰이거나 다른 에러가 발생한다면 false
	public boolean verifyToken(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date()) && !redisTokenUtils.isBlackList(token);
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("잘못된 Jwt 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.error("만료된 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("지원하지 않는 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("잘못된 토큰입니다.");
		}
		return false;
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token).getBody();
	}

	public String getSubject(String token) {
		return parseClaims(token).getSubject();
	}

	public Authentication getAuthentication(String token) {
		if (!StringUtils.hasText(token))
			throw new DaedongException(DaedongStatus.TOKEN_INVALID_EXCEPTION);
		Claims claims = parseClaims(token);

		// 권한 정보가 없음
		if (claims.get(ROLES) == null) {
			throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
		}

		final User user = userService.loadUserByOAuthId(claims.getSubject());

		return new UsernamePasswordAuthenticationToken(user, token,
			List.of(new SimpleGrantedAuthority(user.getRoleType().getCode())));
	}

	public Long getRefreshTokenExpiredDate() {
		return this.refreshTokenExpiredDate;
	}

	public Long getExpiration(String accessToken) {
		// accessToken 남은 유효시간
		Date expiration = parseClaims(accessToken).getExpiration();
		// 현재 시간
		Long now = new Date(System.currentTimeMillis()).getTime();
		return (expiration.getTime() - now);
	}
}
