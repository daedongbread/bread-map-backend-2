package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

/**
 * CommentStatus
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public enum CommentStatus {

	ACTIVE,
	DELETED,
	BLOCKED;

	public static CommentStatus of(final String status) {
		return Arrays.stream(CommentStatus.values())
			.filter(postTopic -> postTopic.name().equals(status))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_COMMENT_STATUS));
	}
}
