package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.exception.RefreshTokenNotFoundException;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.security.token.RefreshToken;
import com.depromeet.breadmapbackend.security.token.RefreshTokenRepository;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtToken reissue(TokenRefreshRequest request) {
        if(!jwtTokenProvider.verifyToken(request.getRefreshToken())) throw new TokenValidFailedException();

        String accessToken = request.getAccessToken();
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(RefreshTokenNotFoundException::new);
        if(!refreshToken.getToken().equals(request.getRefreshToken())) throw new TokenValidFailedException();
        JwtToken reissueToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());
        refreshToken.updateToken(reissueToken.getRefreshToken());

        return reissueToken;
    }

}
