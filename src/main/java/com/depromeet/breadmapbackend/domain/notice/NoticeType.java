package com.depromeet.breadmapbackend.domain.notice;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeType {
    follow,
    reviewLike,
    commentLike,
    comment;
}
