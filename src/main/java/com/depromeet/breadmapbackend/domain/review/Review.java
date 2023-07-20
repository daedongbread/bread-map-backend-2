package com.depromeet.breadmapbackend.domain.review;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Formula;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;

	@Column(nullable = false, length = 200)
	private String content;

	//    @Convert(converter = StringListConverter.class)
	//    private List<String> imageList = new ArrayList<>();

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewImage> imageList = new ArrayList<>();

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isNew = Boolean.TRUE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isHide = Boolean.FALSE; // 관리자 신규 리뷰 숨김

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isDelete = Boolean.FALSE;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewProductRating> ratings = new ArrayList<>();

	@Formula("(SELECT count(*) FROM review_like rl WHERE rl.review_id = id)")
	private Integer likeNum;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewComment> comments = new ArrayList<>();

	@Builder
	private Review(User user, Bakery bakery, String content) {
		this.user = user;
		this.bakery = bakery;
		this.content = content;
	}

	public Double getAverageRating() {
		return Math.floor(this.ratings.stream()
			.mapToLong(ReviewProductRating::getRating).average().orElse(0) * 10) / 10.0;
	}

	public void changeBlock() {
		this.isBlock = !this.isBlock;
	}

	public void removeComment(ReviewComment reviewComment) {
		this.comments.remove(reviewComment);
	}

	public void unNew() {
		this.isNew = Boolean.FALSE;
	}

	public void hide() {
		this.isHide = Boolean.TRUE;
	}

	public void delete() {
		this.isDelete = Boolean.TRUE;
	}

	public boolean isValid() {
		return !(this.isBlock || this.isDelete);
	}

	public boolean isUser(User user) {
		return this.user.equals(user);
	}
}
