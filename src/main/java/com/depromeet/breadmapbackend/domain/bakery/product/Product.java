package com.depromeet.breadmapbackend.domain.bakery.product;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private ProductType productType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String price;

	private String image;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewProductRating> reviewProductRatingList = new ArrayList<>();

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private boolean isTrue;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id")
	private ProductAddReport productAddReport;

	@Builder
	private Product(Long id, ProductType productType, String name, String price, String image, Bakery bakery,
					Boolean isTrue, ProductAddReport productAddReport) {
		this.id = id;
		this.productType = productType;
		this.name = name;
		this.price = price;
		this.image = image;
		this.bakery = bakery;
		if (isTrue == null)
			this.isTrue = true;
		else
			this.isTrue = isTrue;
		this.bakery.getProductList().add(this);
		this.productAddReport = productAddReport;
	}

	public void update(ProductType productType, String name, String price, String image) {
		this.productType = productType;
		this.name = name;
		this.price = price;
		this.image = image;
	}

	public Double getAverageRating() {
		return Math.floor(this.reviewProductRatingList.stream()
				.mapToLong(ReviewProductRating::getRating).average().orElse(0) * 10) / 10.0;
	}
}
