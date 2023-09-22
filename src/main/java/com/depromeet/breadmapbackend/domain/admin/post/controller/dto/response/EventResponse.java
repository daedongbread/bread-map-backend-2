package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;

/**
 * CreateEventRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record EventResponse(
	Long managerId,
	boolean isPosted,
	boolean isFixed,
	String title,
	String content,
	List<String> images
) {

	public static EventResponse of(final PostManagerMapper managerMapper) {
		return new EventResponse(
			managerMapper.getId(),
			managerMapper.isPosted(),
			managerMapper.isFixed(),
			managerMapper.getPost().getTitle(),
			managerMapper.getPost().getContent(),
			managerMapper.getPost().getImages()
				.stream().map(PostImage::getImage).toList()
		);
	}
}
