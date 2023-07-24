package com.depromeet.breadmapbackend.domain.admin.post.domain.service;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.CreateEventCommand;

/**
 * PostAdminService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface PostAdminService {
	Page<PostManagerMapper> getPosts(int page);

	PostManagerMapper createEventPost(CreateEventCommand command);

	boolean canFixEvent();
}
