package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import com.depromeet.breadmapbackend.web.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtToken refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken != null && jwtTokenProvider.verifyToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(UserNotFoundException::new);

            JwtToken jwtToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());

            return jwtToken;
        }

        System.out.println("error");

        throw new TokenValidFailedException();
    }

}
