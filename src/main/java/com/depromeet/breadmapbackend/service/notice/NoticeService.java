package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;

import java.util.List;

public interface NoticeService {
    List<NoticeDto> getNoticeList(String username);
}
