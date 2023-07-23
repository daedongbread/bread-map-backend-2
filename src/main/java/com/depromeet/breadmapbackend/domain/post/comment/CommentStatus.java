package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * CommentStatus
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
@RequiredArgsConstructor
@Getter
public enum CommentStatus {

	ACTIVE(null),
	DELETED("삭제된 댓글입니다."),
	BLOCKED("차단된 댓글입니다.");

	private final String content;

	public String replaceContent(final String content) {
		return this.content == null ? content : this.content;
	}

	public static CommentStatus of(final String status) {
		return Arrays.stream(CommentStatus.values())
			.filter(postTopic -> postTopic.name().equals(status))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.INVALID_COMMENT_STATUS));
	}
}
