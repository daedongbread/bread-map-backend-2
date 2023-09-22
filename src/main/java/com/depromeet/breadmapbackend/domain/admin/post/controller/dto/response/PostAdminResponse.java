package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response;

import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;

/**
 * PostAdminResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record PostAdminResponse(
	Long managerId,
	String nickname,
	Long userId,
	String title,
	boolean isFixed,
	boolean isPosted,
	String createdAt

) {

	public PostAdminResponse(final PostManagerMapperInfo info) {
		this(
			info.managerId(),
			info.nickname(),
			info.userId(),
			info.title(),
			info.isFixed(),
			info.isPosted(),
			info.createdAt()
		);
	}

}
