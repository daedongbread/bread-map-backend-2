package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FeedContentInfo {
    private Long feedId;
    private String feedTitle;
    private String authorName;
    private LocalDateTime activeTime;
    private String isActive;

    @Builder
    public FeedContentInfo(Long feedId, String feedTitle, String authorName, LocalDateTime activeTime, String isActive) {
        this.feedId = feedId;
        this.feedTitle = feedTitle;
        this.authorName = authorName;
        this.activeTime = activeTime;
        this.isActive = isActive;
    }

    public static FeedContentInfo of(Feed feed) {
        return FeedContentInfo.builder()
                .feedId(feed.getId())
                .feedTitle(feed.getSubTitle())
                .authorName(feed.getAdmin().getEmail())
                .activeTime(feed.getActiveTime())
                .isActive(feed.getActivated() == FeedStatus.POSTING ? "게시중" : "미게시")
                .build();
    }
}
