package com.depromeet.breadmapbackend.domain.notice;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeType {
    FOLLOW("팔로우"),
    REVIEW_COMMENT("리뷰 댓글"),
    REVIEW_LIKE("리뷰 좋아요"),
    RECOMMENT("대댓글"),
    REVIEW_COMMENT_LIKE("리뷰 댓글 좋아요"),
    ADD_BAKERY("제보한 빵집 추가"),
    ADD_BREAD("제보한 빵 추가"),
    FLAG_BAKERY_CHANGE("즐겨찾기 빵집 변동사항"),
    FLAG_BAKERY_ADMIN_NOTICE("즐겨찾기 빵집 관리자 새 글");

    private final String code;
}
