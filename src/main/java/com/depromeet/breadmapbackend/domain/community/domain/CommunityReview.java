package com.depromeet.breadmapbackend.domain.community.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;

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

}
