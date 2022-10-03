package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    private List<NoticeDto> todayNoticeList;
    private List<NoticeDto> weekNoticeList;
    private List<NoticeDto> beforeNoticeList;

    @Builder
    public NoticeListDto(List<NoticeDto> todayNoticeList, List<NoticeDto> weekNoticeList, List<NoticeDto> beforeNoticeList) {
        this.todayNoticeList = todayNoticeList;
        this.weekNoticeList = weekNoticeList;
        this.beforeNoticeList = beforeNoticeList;
    }
}
