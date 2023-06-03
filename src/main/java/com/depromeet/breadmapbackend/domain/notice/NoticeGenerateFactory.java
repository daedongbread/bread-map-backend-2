package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;

public interface NoticeGenerateFactory {
	NoticeDto generateNoticeDtoForApi(Notice notice, boolean isFollow);

	NoticeFcmDto generateNoticeDtoForFcm(Notice notice);

}
