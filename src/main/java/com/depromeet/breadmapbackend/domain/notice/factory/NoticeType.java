package com.depromeet.breadmapbackend.domain.notice.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeType {
	FOLLOW("팔로우"),
	REVIEW_COMMENT("리뷰 댓글"),
	REVIEW_LIKE("리뷰 좋아요"),
	RECOMMENT("대댓글"),
	COMMENT_LIKE("댓글 좋아요"),
	REPORT_BAKERY_ADDED("제보한 빵집 추가"),
	ADD_PRODUCT("제보한 상품 추가"),
	FLAG_BAKERY_CHANGE("즐겨찾기 빵집 변동사항"),
	FLAG_BAKERY_ADMIN_NOTICE("즐겨찾기 빵집 관리자 새 글"),
	EVENT("이벤트"),
	BAKERY_ADDED("빵집 추가"),
	COMMUNITY_LIKE("커뮤니티글 좋아요"),
	COMMUNITY_COMMENT("커뮤니티 댓글"),
	CURATION("큐레이션");

	private final String code;

}
