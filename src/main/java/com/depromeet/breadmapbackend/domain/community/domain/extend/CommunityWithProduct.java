package com.depromeet.breadmapbackend.domain.community.domain.extend;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.community.domain.Community;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.TempBakerySource;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.user.User;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
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
@Table(name = "community_product")
@Getter
@DiscriminatorValue(value = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityWithProduct extends Community {

    // @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<ReviewProductRating> ratings = new ArrayList<>();

    private CommunityWithProduct(
        final User user,
        final CommunityType communityType,
        final String title,
        final String content,
        final List<String> images,
        final Bakery bakery,
        final TempBakerySource tempBakerySource,
        final List<ReviewProductRating> ratings
    ) {
        super(user, communityType, title, content, bakery);
        // this.ratings = ratings;
    }
}
