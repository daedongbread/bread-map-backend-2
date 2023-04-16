package com.depromeet.breadmapbackend.domain.notice;

import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.notice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void addNoticeToken(
//            @CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) NoticeTokenRequest request) {
//        noticeService.addNoticeToken(oAuthId, request);
//    }

    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PageResponseDto<NoticeDto>> getNoticeList(
            @CurrentUser String oAuthId, @PathVariable NoticeDayType type, @RequestParam(required = false) Long lastId, @RequestParam int page) {
        return new ApiResponse<>(noticeService.getNoticeList(oAuthId, type, lastId, page));
    }
}
