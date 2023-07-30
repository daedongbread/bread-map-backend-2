package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponseForAdmin {

	private int totalPages;
	private long totalElements;
	private List<FeedContentInfo> contents;


	@Builder
	private FeedResponseForAdmin(int totalPages, long totalElements, List<Feed> feeds) {
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.contents = feeds.stream()
					.map(FeedContentInfo::of)
					.collect(Collectors.toList());
	}

	public static FeedResponseForAdmin of(int totalPages, long totalElements, List<Feed> feeds) {
		return FeedResponseForAdmin.builder()
			.totalPages(totalPages)
			.totalElements(totalElements)
			.feeds(feeds)
			.build();
	}
}
