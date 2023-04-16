package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowUserDto;

import java.util.List;

public interface UserFollowService {
    void follow(String oAuthId, FollowRequest request);
    void unfollow(String oAuthId, FollowRequest request);
    List<FollowUserDto> followerList(String oAuthId, Long userId);
    List<FollowUserDto> followingList(String oAuthId, Long userId);
}
