package com.depromeet.breadmapbackend.web.controller.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    private List<NoticeDto> todayNoticeDtoList;
    private List<NoticeDto> weekNoticeDtoList;
    private List<NoticeDto> beforeNoticeDtoList;

    @Builder
    public NoticeListDto(List<NoticeDto> todayNoticeDtoList, List<NoticeDto> weekNoticeDtoList, List<NoticeDto> beforeNoticeDtoList) {
        this.todayNoticeDtoList = todayNoticeDtoList;
        this.weekNoticeDtoList = weekNoticeDtoList;
        this.beforeNoticeDtoList = beforeNoticeDtoList;
    }
}
