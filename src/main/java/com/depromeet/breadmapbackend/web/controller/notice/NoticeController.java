package com.depromeet.breadmapbackend.web.controller.notice;

import com.depromeet.breadmapbackend.service.notice.NoticeService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeListDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenAlarmDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.notice.dto.UpdateNoticeTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addNoticeToken(@CurrentUser String username, @RequestBody NoticeTokenRequest request) {
        noticeService.addNoticeToken(username, request);
    }

    @GetMapping("/token/alarm")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<NoticeTokenAlarmDto> getNoticeTokenAlarm(@CurrentUser String username, @RequestBody NoticeTokenRequest request) {
        return new ApiResponse<>(noticeService.getNoticeTokenAlarm(username, request));
    }

    @PatchMapping("/token/alarm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNoticeTokenAlarm(@CurrentUser String username, @RequestBody NoticeTokenRequest request) {
        noticeService.updateNoticeTokenAlarm(username, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<NoticeListDto> getNoticeList(@CurrentUser String username) { // TODO : 병렬처리
        return new ApiResponse<>(NoticeListDto.builder()
                .todayNoticeList(noticeService.getTodayNoticeList(username))
                .weekNoticeList(noticeService.getWeekNoticeList(username))
                .beforeNoticeList(noticeService.getBeforeNoticeList(username)).build());
    }
}
