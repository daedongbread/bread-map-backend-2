package com.depromeet.breadmapbackend.domain.community.domain.extend;

import java.util.List;

import javax.persistence.Entity;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.community.domain.Community;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityImage;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.TempBakerySource;
import com.depromeet.breadmapbackend.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CommunityEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityEvent extends Community {

	public CommunityEvent(final User user,
		final CommunityType communityType, final String title,
		final String content, final List<CommunityImage> images,
		final Bakery bakery,
		final TempBakerySource tempBakerySource) {
		super(user, communityType, title, content, images, bakery, tempBakerySource);
	}
}
