package com.depromeet.breadmapbackend.domain.post.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * CommentStatusForResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/26
 */
@RequiredArgsConstructor
@Getter
public enum CommentResponseStatus {

	ACTIVE(null),
	DELETED("삭제된 댓글입니다."),
	BLOCKED_BY_ADMIN("관리자에 의해 차단된 댓글입니다."),
	BLOCKED_BY_USER("차단된 유저의 댓글 입니다."),
	;

	private final String content;

	public String replaceContent(final String content) {
		return this.content == null ? content : this.content;
	}
}
