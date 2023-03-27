package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowUserDto;

import java.util.List;

public interface UserFollowService {
    void follow(String username, FollowRequest request);
    void unfollow(String username, FollowRequest request);
    List<FollowUserDto> followerList(String username, Long userId);
    List<FollowUserDto> followingList(String username, Long userId);
}
