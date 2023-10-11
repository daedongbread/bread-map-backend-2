package com.depromeet.breadmapbackend.domain.notice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeFcmDto;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeTokenRepository;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

@Validated(ValidationSequence.class)
@RestController
@RequestMapping("/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
	private final NoticeService noticeService;
	private final FcmService fcmService;
	private final NoticeTokenRepository noticeTokenRepository;
	//    @PostMapping
	//    @ResponseStatus(HttpStatus.NO_CONTENT)
	//    public void addNoticeToken(
	//            @CurrentUser String oAuthId, @RequestBody @Validated(ValidationSequence.class) NoticeTokenRequest request) {
	//        noticeService.addNoticeToken(oAuthId, request);
	//    }

	@GetMapping("/fcm")
	public void test() throws FirebaseMessagingException {

		List<String> tokens = noticeTokenRepository.findByUser(76L).stream().map(NoticeToken::getDeviceToken).toList();

		fcmService.sendMessageTo(NoticeFcmDto.builder()
			.fcmTokens(tokens)
			.title("노티 테스트")
			.contentId(12L)
			.type(NoticeType.FOLLOW)
			.build());
	}

	@GetMapping("/{type}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PageResponseDto<NoticeDto>> getNoticeList(@CurrentUser String oAuthId,
		@PathVariable @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class) NoticeDayType type,
		@RequestParam(required = false) Long lastId, @RequestParam int page) {
		return new ApiResponse<>(noticeService.getNoticeList(oAuthId, type, lastId, page));
	}
}
