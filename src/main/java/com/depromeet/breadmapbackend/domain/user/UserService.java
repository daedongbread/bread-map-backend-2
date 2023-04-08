package com.depromeet.breadmapbackend.domain.user;


import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.user.dto.*;

public interface UserService {
    JwtToken reissue(ReissueRequest reissueRequest);
    ProfileDto profile(String username, Long userId);
    void updateNickName(String username, UpdateNickNameRequest request);
    void logout(LogoutRequest reissueRequest);
//    void deleteUser(String username);
    AlarmDto getAlarmStatus(String username);
    void alarmChange(String username, NoticeTokenRequest request);
}
