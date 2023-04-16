package com.depromeet.breadmapbackend.domain.user;


import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.user.dto.*;

public interface UserService {
    ProfileDto profile(String oAuthId, Long userId);
    void updateNickName(String oAuthId, UpdateNickNameRequest request);
//    void deleteUser(String username);
    AlarmDto getAlarmStatus(String oAuthId);
    void alarmChange(String oAuthId, NoticeTokenRequest request);
}
