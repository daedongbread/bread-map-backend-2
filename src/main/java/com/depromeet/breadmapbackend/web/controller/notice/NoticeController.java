package com.depromeet.breadmapbackend.web.controller.notice;

import com.depromeet.breadmapbackend.service.notice.NoticeService;
import com.depromeet.breadmapbackend.web.controller.common.ApiResponse;
import com.depromeet.breadmapbackend.web.controller.common.CurrentUser;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<NoticeDto>> getNoticeList(@CurrentUser String username) {
        return new ApiResponse<>(noticeService.getNoticeList(username));
    }
}
