package com.depromeet.breadmapbackend.domain.community.domain.extend;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.community.domain.Community;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityImage;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.TempBakerySource;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CommunityReview
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityReview extends Community {

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewProductRating> ratings = new ArrayList<>();

	public CommunityReview(final User user,
		final CommunityType communityType, final String title,
		final String content, final List<CommunityImage> images,
		final Bakery bakery,
		final TempBakerySource tempBakerySource,
		final List<ReviewProductRating> ratings) {
		super(user, communityType, title, content, images, bakery, tempBakerySource);
		this.ratings = ratings;
	}
}
