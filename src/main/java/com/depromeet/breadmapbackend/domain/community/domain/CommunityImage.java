package com.depromeet.breadmapbackend.domain.community.domain;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CommunityImage
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityImage {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY, optional = false)
	@JoinColumn(name = "community_id")
	private Community community;

	@Column(nullable = false)
	private String image;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isNew = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isRegistered = Boolean.FALSE;

	private CommunityImage(Community community, String image) {
		this.community = community;
		this.image = image;
		this.isRegistered = true;
		this.isNew = true;
	}

	public static CommunityImage createCommunityImage(Community community, String image) {
		return new CommunityImage(community, image);
	}

	public void unNew() {
		this.isNew = false;
	}

	public void unregister() {
		this.isRegistered = false;
		this.isNew = false;
	}
}
