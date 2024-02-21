package com.depromeet.breadmapbackend.domain.community.domain.extend;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.community.domain.Community;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.user.User;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CommunityBaking
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
@Entity
@Table(name = "community_no_product")
@Getter
@DiscriminatorValue(value = "NO_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityWithOutProduct extends Community {

    @Column(nullable = false, length = 100)
    private String title;

    private CommunityWithOutProduct(
        final User user,
        final CommunityType communityType,
        final String title,
        final String content,
        final Bakery bakery
    ) {
        super(user, communityType, content, bakery);
        this.title = title;
    }

    public static CommunityWithOutProduct createCommunityBaking(
        final User user,
        final CommunityType communityType,
        final String title,
        final String content,
        final List<String> images,
        final Bakery bakery
    ) {
        final CommunityWithOutProduct communityWithOutProduct = new CommunityWithOutProduct(user, communityType,
            title, content, bakery);
        if (images != null && !images.isEmpty()) {
            communityWithOutProduct.addImages(images);
        }
        return communityWithOutProduct;
    }
}
