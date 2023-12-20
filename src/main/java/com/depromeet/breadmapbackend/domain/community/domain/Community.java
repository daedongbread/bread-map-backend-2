package com.depromeet.breadmapbackend.domain.community.domain;

import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Community
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "communityType")
public abstract class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private CommunityType communityType;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String content;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityImage> images = new ArrayList<>();

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "temp_bakery_source_id")
	private TempBakerySource tempBakerySource;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isHide = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isDelete = Boolean.FALSE;

	public Community(
		final User user,
		final CommunityType communityType,
		final String title,
		final String content,
		final List<CommunityImage> images,
		final Bakery bakery,
		final TempBakerySource tempBakerySource
	) {
		this.user = user;
		this.communityType = communityType;
		this.title = title;
		this.content = content;
		this.images = images;
		this.bakery = bakery;
		this.tempBakerySource = tempBakerySource;
		this.isBlock = isBlock;
		this.isHide = isHide;
		this.isDelete = isDelete;
	}
}
