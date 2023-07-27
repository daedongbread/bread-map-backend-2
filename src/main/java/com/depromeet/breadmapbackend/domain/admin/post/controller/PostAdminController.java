package com.depromeet.breadmapbackend.domain.admin.post.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request.EventRequest;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request.UpdateEventOrderRequest;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.EventCarouselResponse;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.PostAdminResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.service.PostAdminService;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * PostAdminController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */

@RestController
@RequestMapping("/v1/admin/posts")
@RequiredArgsConstructor
public class PostAdminController {

	private final PostAdminService postAdminService;

	// 커뮤니티 조회 with 고점됨, 처리상태, 케러셀 (처리상태 게시중인 경우만 커뮤니티에 조회)
	// id, 작성자, 제목, 작성일자, 고정, 처리상태, 캐러셀
	@GetMapping("/{page}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<PostAdminResponse>> getPosts(
		@PathVariable("page") final int page
	) {
		return new ApiResponse<>(
			Mapper.of(postAdminService.getEventPosts(page))
		);
	}

	// 이벤트 등록 기능 ( 배너이미지 추가, 사용자 계정 하드코딩 ) 이미지 최대 10개
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	void createEventPost(
		@RequestBody @Valid final EventRequest request
	) {
		postAdminService.createEventPost(Mapper.of(request));
	}

	// 이벤트 수정
	@PatchMapping("/{managerId}")
	@ResponseStatus(HttpStatus.OK)
	void updateEventPost(
		@PathVariable("managerId") Long managerId,
		@RequestBody @Valid final EventRequest request
	) {
		postAdminService.updateEventPost(Mapper.of(request), managerId);
	}

	// 고정 가능 여부
	@GetMapping("/can-fix")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<Boolean> canFixEvent() {
		return new ApiResponse<>(
			postAdminService.canFixEvent()
		);
	}

	// 캐러셀 순서 변경
	@PatchMapping("/order")
	@ResponseStatus(HttpStatus.OK)
	void updateEventOrder(
		@RequestBody @Valid final List<UpdateEventOrderRequest> request
	) {
		postAdminService.updateEventOrder(request.stream().map(Mapper::of).toList());
	}

	@GetMapping("/carousels")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<List<EventCarouselResponse>> getCarousels() {
		return new ApiResponse<>(postAdminService.getCarousels()
			.stream()
			.map(Mapper::of)
			.toList()
		);
	}
}
