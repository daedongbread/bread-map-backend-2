package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;

public interface NoticeGenerateFactory {
	NoticeDto generateNotice(Notice notice, boolean isFollow);

	String getTitle(Notice notice);
}
