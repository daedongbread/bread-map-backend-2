package com.depromeet.breadmapbackend.web.controller.notice;

import com.depromeet.breadmapbackend.service.notice.NoticeService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.notice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<NoticeDto>> getTodayNoticeList(
            @CurrentUser String username,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(noticeService.getTodayNoticeList(username, pageable));
    }

    @GetMapping("/week")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<NoticeDto>> getWeekNoticeList(
            @CurrentUser String username,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(noticeService.getWeekNoticeList(username, pageable));
    }

    @GetMapping("/before")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<NoticeDto>> getBeforeNoticeList(
            @CurrentUser String username,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ApiResponse<>(noticeService.getBeforeNoticeList(username, pageable));
    }
}
