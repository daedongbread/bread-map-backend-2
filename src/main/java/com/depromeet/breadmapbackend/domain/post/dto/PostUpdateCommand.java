package com.depromeet.breadmapbackend.domain.post.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.PostTopic;

/**
 * PostUpdateCommand
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
public record PostUpdateCommand(
	Long postId,
	String title,
	String content,
	PostTopic postTopic,
	List<String> images
) {

}
