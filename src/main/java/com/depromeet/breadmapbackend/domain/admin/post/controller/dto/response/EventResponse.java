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
	boolean isCarousel,
	String title,
	String content,
	String bannerImage,
	List<String> images
) {

	public static EventResponse of(final PostManagerMapper managerMapper) {
		return new EventResponse(
			managerMapper.getId(),
			managerMapper.isPosted(),
			managerMapper.isFixed(),
			managerMapper.isCarousel(),
			managerMapper.getPost().getTitle(),
			managerMapper.getPost().getContent(),
			managerMapper.getBannerImage(),
			managerMapper.getPost().getImages()
				.stream().map(PostImage::getImage).toList()
		);
	}
}
