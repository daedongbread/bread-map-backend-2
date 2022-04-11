package com.depromeet.breadmapbackend.service.user;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.JwtTokenProvider;
import com.depromeet.breadmapbackend.web.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.depromeet.breadmapbackend.security.util.HeaderConstant.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtToken refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader(HEADER_REFRESH_TOKEN);

        if (refreshToken != null && jwtTokenProvider.verifyToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(UserNotFoundException::new);

            JwtToken jwtToken = jwtTokenProvider.createJwtToken(username, user.getRoleType().getCode());

            return jwtToken;
        }

        throw new TokenValidFailedException();
    }

}
