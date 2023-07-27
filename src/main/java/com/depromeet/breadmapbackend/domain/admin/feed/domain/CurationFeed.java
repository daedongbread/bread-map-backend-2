package com.depromeet.breadmapbackend.domain.admin.feed.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;

import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "curation")
@Getter
@DiscriminatorValue(value = "CURATION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationFeed extends Feed {

	@Embedded
	private CurationBakeries bakeries;

	@Embedded
	private FeedLikes likes;

	@Builder
	public CurationFeed(Admin admin, Category category,
		String subTitle, String introduction, String conclusion, String thumbnailUrl, String feedType,
		LocalDateTime activeTime, FeedStatus activated, CurationBakeries bakeries, FeedLikes likes) {
		super(admin, category, subTitle, introduction, conclusion, thumbnailUrl, feedType, activeTime, activated);
		this.bakeries = new CurationBakeries(new ArrayList<>());
		this.likes = new FeedLikes(new ArrayList<>());
	}

	public void addAll(List<Bakery> bakeries, FeedRequestDto requestDto) {
		this.bakeries.addAll(this, bakeries, requestDto);
	}

	public void removeAllBakeries() {
		this.bakeries.clear();
	}

	public void update(List<Bakery> bakeries, Category category, FeedRequestDto updateDto) {
		super.update(category, updateDto.getCommon());

		this.removeAllBakeries();
		this.addAll(bakeries, updateDto);
	}

	public int getLikeCount() {
		return this.likes.getCounts();
	}

	public int getLikeCountByUser(Long userId) {
		return this.likes.getCountsByUser(userId);
	}

	public void like(User user) {
		FeedLike like = new FeedLike(user, this);
		FeedLike feedLike = likes.find(like);

		likes.like(feedLike);
	}

	public void unLike(User user) {
		FeedLike like = new FeedLike(user, this);
		FeedLike feedLike = likes.find(like);

		likes.unLike(feedLike);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CurationFeed feed = (CurationFeed)o;
		return Objects.equals(id, feed.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
