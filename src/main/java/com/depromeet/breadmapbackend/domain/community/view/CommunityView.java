package com.depromeet.breadmapbackend.domain.community.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.community.domain.Community;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityView extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private Community community;

	@Column(nullable = false)
	private Integer viewNum;

	@Builder
	public CommunityView(Community community) {
		this.community = community;
		this.viewNum = 0;
	}

	public void viewReview() {
		this.viewNum += 1;
	}
}
