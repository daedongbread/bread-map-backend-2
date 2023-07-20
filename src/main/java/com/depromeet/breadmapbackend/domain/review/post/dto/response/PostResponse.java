package com.depromeet.breadmapbackend.domain.review.post.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.review.post.Post;
import com.depromeet.breadmapbackend.domain.review.post.PostTopic;

public record PostResponse(
	Long postId,
	PostTopic postTopic,
	String title,
	UserInfo writerInfo,
	List<String> images,
	String content,
	long likeCount,
	long commentCount,
	LocalDate createdDate

) {
	public static PostResponse of(final Post postBy) {
		throw new IllegalStateException("PostResponse::of not implemented yet");
	}

	public record UserInfo(
		Long userId,
		String nickname,
		String profileImage,
		long reviewCount,
		long followerCount,
		boolean isFollowed
	) {
	}
}
