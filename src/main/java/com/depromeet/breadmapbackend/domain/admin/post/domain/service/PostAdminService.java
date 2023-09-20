package com.depromeet.breadmapbackend.domain.admin.post.domain.service;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.EventResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;

/**
 * PostAdminService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface PostAdminService {
	Page<PostManagerMapperInfo> getEventPosts(int page);

	PostManagerMapper createEventPost(EventCommand command);

	boolean canFixEvent();

	void updateEventPost(EventCommand command, Long managerId);

	EventResponse getEventPost(Long managerId);
}
