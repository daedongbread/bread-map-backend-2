package com.depromeet.breadmapbackend.domain.admin.post.controller;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request.CreateEventRequest;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.PostAdminResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.CreateEventCommand;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public class Mapper {
	public static PageResponseDto<PostAdminResponse> of(final Page<PostManagerMapper> postManagerMappers) {
		return PageResponseDto.of(postManagerMappers, PostAdminResponse::new);

	}

	public static CreateEventCommand of(final CreateEventRequest request) {
		return new CreateEventCommand(
			request.isPosted(),
			request.isFixed(),
			request.isCarousel(),
			request.title(),
			request.content(),
			request.bannerImage(),
			request.images()
		);
	}
}
