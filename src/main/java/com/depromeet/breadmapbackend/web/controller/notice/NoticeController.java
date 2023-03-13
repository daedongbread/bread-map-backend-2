package com.depromeet.breadmapbackend.web.controller.notice;

import com.depromeet.breadmapbackend.domain.notice.NoticeDayType;
import com.depromeet.breadmapbackend.service.notice.NoticeService;
import com.depromeet.breadmapbackend.web.advice.ValidationSequence;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addNoticeToken(
            @CurrentUser String username, @RequestBody @Validated(ValidationSequence.class) NoticeTokenRequest request) {
        noticeService.addNoticeToken(username, request);
    }

    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<NoticeDto>> getNoticeList(
            @CurrentUser String username, @PathVariable NoticeDayType type, @RequestParam(required = false) Long lastId, @RequestParam int page) {
        return new ApiResponse<>(noticeService.getNoticeList(username, type, lastId, page));
    }
}
