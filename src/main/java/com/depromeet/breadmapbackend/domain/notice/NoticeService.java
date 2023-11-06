package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface NoticeService {
	PageResponseDto<NoticeDto> getNoticeList(String oAuthId, int page);
}
