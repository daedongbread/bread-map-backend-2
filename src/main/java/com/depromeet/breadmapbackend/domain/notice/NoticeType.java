package com.depromeet.breadmapbackend.domain.notice;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeType {
    follow,
    reviewComment,
    reviewLike,
    recomment,
    reviewCommentLike,
    addBakery,
    addBread,
    flagBakeryChange,
    flagBakeryAdminNotice;
}
