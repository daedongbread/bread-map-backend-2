package com.depromeet.breadmapbackend.service.user;


import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.FollowDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.FollowRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.ProfileDto;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    JwtToken reissue(TokenRefreshRequest tokenRefreshRequest);
    ProfileDto profile(String username);
    void follow(String username, FollowRequest request);
    void unfollow(String username, FollowRequest request);
    List<FollowDto> followerList(String username);
    List<FollowDto> followingList(String username);
}
