package com.depromeet.breadmapbackend.service.user;


import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    JwtToken reissue(ReissueRequest reissueRequest);
    ProfileDto profile(String username, Long userId);
    void updateNickName(String username, UpdateNickNameRequest request, MultipartFile file) throws IOException;
    void logout(LogoutRequest reissueRequest);
    void deleteUser(String username);
    void follow(String username, FollowRequest request);
    void unfollow(String username, FollowRequest request);
    List<FollowUserDto> followerList(String username, Long userId);
    List<FollowUserDto> followingList(String username, Long userId);
    List<BlockUserDto> blockList(String username);
    void block(String username, BlockRequest request);
    void unblock(String username, BlockRequest request);
    AlarmDto getAlarmStatus(String username);
    void updateAlarmStatus(String username);
}
