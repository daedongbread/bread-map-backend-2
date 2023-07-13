package com.depromeet.breadmapbackend.domain.notice;

public interface NoticeContentProcessor {
	String getImage(Notice notice);

	String getTitle(Notice notice);
}
