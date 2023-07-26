package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.DateTimeParser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "feedType")
public abstract class Feed extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	private String subTitle;

	private String introduction;

	private String conclusion;

	private String thumbnailUrl;

	@Column(name = "feedType", insertable = false, updatable = false)
	private String feedType;

	private LocalDateTime activeTime;

	@Enumerated(value = EnumType.STRING)
	private FeedStatus activated;

	protected void update(Category category, CommonFeedRequestDto updateDto) {
		this.category = category;
		this.subTitle = updateDto.getSubTitle();
		this.introduction = updateDto.getIntroduction();
		this.conclusion = updateDto.getConclusion();
		this.thumbnailUrl = updateDto.getThumbnailUrl();
		this.activeTime = DateTimeParser.parse(updateDto.getActiveTime());
		this.feedType = updateDto.getFeedType().toString();
	}

	public void delete() {
		this.activated = FeedStatus.INACTIVATED;
	}

	public Feed(Admin admin, Category category, String subTitle, String introduction, String conclusion,
		String thumbnailUrl, String feedType, LocalDateTime activeTime, FeedStatus activated) {
		this.admin = admin;
		this.category = category;
		this.subTitle = subTitle;
		this.introduction = introduction;
		this.conclusion = conclusion;
		this.thumbnailUrl = thumbnailUrl;
		this.feedType = feedType;
		this.activeTime = activeTime;
		this.activated = activated;
	}
}
