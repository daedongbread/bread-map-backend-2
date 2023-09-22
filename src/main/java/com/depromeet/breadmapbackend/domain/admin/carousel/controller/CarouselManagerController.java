package com.depromeet.breadmapbackend.domain.admin.carousel.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.carousel.controller.dto.Mapper;
import com.depromeet.breadmapbackend.domain.admin.carousel.controller.dto.UpdateCarouselOrderRequest;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.service.CarouselManagerService;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.EventCarouselResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * CarouselController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */

@RequiredArgsConstructor
@RestController
public class CarouselManagerController {

	private final CarouselManagerService carouselManagerService;

	// 캐러셀 순서 변경
	@PatchMapping("/order")
	@ResponseStatus(HttpStatus.OK)
	void updateEventOrder(
		@RequestBody @Valid final List<UpdateCarouselOrderRequest> request
	) {
		carouselManagerService.updateCarouselOrder(request.stream().map(Mapper::of).toList());
	}

	// 	@GetMapping("/v1/posts/carousels") migrate
	@GetMapping("/carousels")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<List<EventCarouselResponse>> getCarousels() {
		return new ApiResponse<>(carouselManagerService.getCarousels()
			.stream()
			.map(Mapper::of)
			.toList()
		);
	}
}
