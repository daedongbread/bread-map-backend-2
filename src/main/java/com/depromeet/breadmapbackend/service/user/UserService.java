package com.depromeet.breadmapbackend.service.user;


import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
public interface UserService {

    JwtToken refresh(TokenRefreshRequest tokenRefreshRequest);

}
