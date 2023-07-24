package com.depromeet.breadmapbackend.domain.bakery.view;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@IdClass(BakeryViewId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryView  {
	@Id
	@Column(name = "bakery_id" )
	private Long bakeryId;

	@Id
	@Column(columnDefinition = "DATE")
	private LocalDate viewDate;

	@Column(nullable = false)
	private Long viewCount;


	@Builder
	public BakeryView(final Long bakeryId, final LocalDate viewDate, final Long viewCount) {
		this.bakeryId = bakeryId;
		this.viewCount = viewCount;
		this.viewDate = viewDate;
	}

}
