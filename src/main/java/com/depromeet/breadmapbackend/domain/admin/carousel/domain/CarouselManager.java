package com.depromeet.breadmapbackend.domain.admin.carousel.domain;

import static javax.persistence.GenerationType.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Carousel
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/20
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarouselManager extends BaseEntity {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private Long targetId;
	private String bannerImage;
	private int carouselOrder;
	private CarouselType carouselType;
	private boolean carouseled = false;

	@Builder
	public CarouselManager(
		final Long targetId,
		final String bannerImage,
		final int carouselOrder,
		final CarouselType carouselType,
		final boolean carouseled
	) {
		this.targetId = targetId;
		this.bannerImage = bannerImage;
		this.carouselOrder = carouselOrder;
		this.carouselType = carouselType;
		this.carouseled = carouseled;

	}

	public void updateCarouselOrder(final Integer carouselOrder) {
		this.carouselOrder = carouselOrder;
	}

	public void toggleCarousel(final boolean isCarousel, final int carouselOrder) {
		this.carouseled = isCarousel;
		this.carouselOrder = carouselOrder;
	}

	public void updateBannerImage(final String bannerImage) {
		this.bannerImage = bannerImage;
	}

}