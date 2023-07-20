package com.depromeet.breadmapbackend.domain.review.post.dto;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.review.post.PostTopic;

public record PostDetailInfo(
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
