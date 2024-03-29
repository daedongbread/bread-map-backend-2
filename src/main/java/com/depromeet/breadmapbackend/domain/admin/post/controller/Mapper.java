package com.depromeet.breadmapbackend.domain.admin.post.controller;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.request.EventRequest;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.PostAdminResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public class Mapper {
	public static PageResponseDto<PostAdminResponse> of(final Page<PostManagerMapperInfo> info) {
		return PageResponseDto.of(info, PostAdminResponse::new);

	}

	public static EventCommand of(final EventRequest request) {
		return new EventCommand(
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
