package com.depromeet.breadmapbackend.domain.notice;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
	private final NoticeService noticeService;
	private final FcmService fcmService;
	private final NoticeTokenRepository noticeTokenRepository;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PageResponseDto<NoticeDto>> getNoticeList(
		@CurrentUser String oAuthId,
		@RequestParam(required = false) Long lastId,
		@RequestParam int page
	) {
		return new ApiResponse<>(noticeService.getNoticeList(oAuthId, lastId, page));
	}
}
