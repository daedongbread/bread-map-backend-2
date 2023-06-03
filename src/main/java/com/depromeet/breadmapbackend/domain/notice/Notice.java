package com.depromeet.breadmapbackend.domain.notice;

import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@Column(name = "user_id")
	private Long userId;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "fromUser_id", insertable = false, updatable = false)
	private User fromUser;

	@Column(name = "fromUser_id")
	private Long fromUserId;
	//    /*
	//     알림 주 내용
	//     */
	//    @Column(nullable = false)
	//    private String title;

	/*
	 내가 쓴 리뷰 아이디 or 내가 쓴 댓글 아이디 or 유저 아이디
	 */
	private Long contentId;

	/*
	 내가 쓴 리뷰 내용(디자인엔 제목으로 나와있음) or 내가 쓴 댓글 내용
	 팔로우/팔로잉 알람은 null
	 */
	private String content;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private NoticeType type;

	@Builder
	public Notice(Long userId, Long fromUserId, Long contentId, String content, NoticeType type) {
		this.userId = userId;
		this.fromUserId = fromUserId;
		this.contentId = contentId;
		this.content = content;
		this.type = type;
	}

}
