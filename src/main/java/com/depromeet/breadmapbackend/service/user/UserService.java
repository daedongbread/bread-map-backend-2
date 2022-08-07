package com.depromeet.breadmapbackend.service.user;


import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;

import java.util.List;

public interface UserService {
    JwtToken reissue(TokenRefreshRequest tokenRefreshRequest);
    ProfileDto profile(String username);
    void deleteUser(String username);
    void follow(String username, FollowRequest request);
    void unfollow(String username, FollowRequest request);
    List<SimpleUserDto> followerList(String username);
    List<SimpleUserDto> followingList(String username);
    List<SimpleUserDto> blockList(String username);
    void block(String username, BlockRequest request);
    void unblock(String username, BlockRequest request);
}
