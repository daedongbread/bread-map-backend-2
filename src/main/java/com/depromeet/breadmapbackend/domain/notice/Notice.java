package com.depromeet.breadmapbackend.domain.notice;

import static javax.persistence.FetchType.*;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String title;

	/*
	 내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 유저 아이디
	 */
	@Nullable
	@Column
	private Long contentId;

	@Nullable
	@Column
	private Long subContentId;

	@Nullable
	@Column
	private String content;

	@Nullable
	@Column
	private String contentParam;

	@Nullable
	@Column
	private String extraParam;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private NoticeType type;

	public static Notice createNoticeWithContent(
		final User user,
		final String title,
		final Long contentId,
		final String content,
		final String contentParam,
		final NoticeType type
	) {
		return Notice.builder()
			.user(user)
			.title(title)
			.contentId(contentId)
			.content(content)
			.contentParam(contentParam)
			.type(type)
			.build();
	}

	public static Notice createNoticeWithContentAndSubContentId(
		final User user,
		final String title,
		final Long contentId,
		final String content,
		final String contentParam,
		final NoticeType type,
		final Long subContentId
	) {
		return Notice.builder()
			.user(user)
			.title(title)
			.contentId(contentId)
			.subContentId(subContentId)
			.content(content)
			.contentParam(contentParam)
			.type(type)
			.build();
	}

	public static Notice createNoticeWithContentAndExtraParam(
		final User user,
		final String title,
		final Long contentId,
		final String content,
		final String contentParam,
		final String extraParam,
		final NoticeType type
	) {
		return Notice.builder()
			.user(user)
			.title(title)
			.contentId(contentId)
			.content(content)
			.contentParam(contentParam)
			.extraParam(extraParam)
			.type(type)
			.build();
	}

	@Builder
	public Notice(final User user, final String title, @Nullable final Long contentId,
		@Nullable final String content, @Nullable final String contentParam,
		@Nullable final String extraParam, final NoticeType type, @Nullable final Long subContentId) {
		this.user = user;
		this.title = title;
		this.subContentId = subContentId;
		this.contentId = contentId;
		this.content = content;
		this.contentParam = contentParam;
		this.extraParam = extraParam;
		this.type = type;
	}
}
