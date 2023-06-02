package com.depromeet.breadmapbackend.domain.notice;

import java.util.function.BiFunction;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeDto;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NoticeType {
	FOLLOW("팔로우", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			notice.getFromUser().getUserInfo().getImage(),
			getNickName(notice) + "님이 회원님을 팔로우하기 시작했어요"))),

	REVIEW_COMMENT("리뷰 댓글", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getComment(),
			getNickName(notice) + "님이 댓글을 달았어요!"))),

	REVIEW_LIKE("리뷰 좋아요", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getLike(),
			"내 리뷰를 " + getNickName(notice) + "님이 좋아해요!"))),

	RECOMMENT("대댓글", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getComment(),
			"내 댓글에 " + getNickName(notice) + "님이 대댓글을 달았어요!"))),

	REVIEW_COMMENT_LIKE("리뷰 댓글 좋아요", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getLike(),
			"내 댓글을 " + getNickName(notice) + "님이 좋아해요!"))),

	ADD_BAKERY("제보한 빵집 추가", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getReport(),
			"내가 제보한 빵집이 추가되었어요!"))),

	ADD_PRODUCT("제보한 상품 추가", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getReport(),
			"내가 제보한 빵이 추가되었어요!"))),

	FLAG_BAKERY_CHANGE("즐겨찾기 빵집 변동사항", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getFlag(),
			""))),

	FLAG_BAKERY_ADMIN_NOTICE("즐겨찾기 빵집 관리자 새 글", ((notice, defaultImage) ->
		new NoticeDto.NoticeBaseInfo(
			defaultImage.getFlag(),
			"관리자 글이 업데이트 됐어요!"))),
	;

	private final String code;

	private final BiFunction<Notice, CustomAWSS3Properties.DefaultImage, NoticeDto.NoticeBaseInfo> getNoticeBaseInfoFunction;

	public String getCode() {
		return code;
	}

	public NoticeDto.NoticeBaseInfo getNoticeBaseInfo(Notice notice, CustomAWSS3Properties.DefaultImage defaultImage) {
		return getNoticeBaseInfoFunction.apply(notice, defaultImage);
	}

	private static String getNickName(final Notice notice) {
		return notice.getFromUser().getNickName();
	}
}
