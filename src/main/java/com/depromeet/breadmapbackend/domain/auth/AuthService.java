package com.depromeet.breadmapbackend.domain.auth;

import com.depromeet.breadmapbackend.domain.auth.dto.LoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.RegisterRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;

public interface AuthService {
    JwtToken login(LoginRequest request);
    JwtToken register(RegisterRequest request);
    JwtToken reissue(ReissueRequest reissueRequest);
    void logout(LogoutRequest reissueRequest);
}
