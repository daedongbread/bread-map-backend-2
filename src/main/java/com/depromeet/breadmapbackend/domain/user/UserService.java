package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.user.dto.AlarmDto;
import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.user.dto.ProfileDto;
import com.depromeet.breadmapbackend.domain.user.dto.UpdateNickNameRequest;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

public interface UserService {
	ProfileDto profile(String oAuthId, Long userId);

	void updateNickName(String oAuthId, UpdateNickNameRequest request);

	//    void deleteUser(String username);
	AlarmDto getAlarmStatus(String oAuthId);

	AlarmDto alarmChange(String oAuthId, NoticeTokenRequest request);

	CurrentUserInfo loadUserInfoByOAuthId(String oAuthId);
}
