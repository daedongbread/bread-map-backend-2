package com.depromeet.breadmapbackend.domain.bakery;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakery;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.StringUtils;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.FacilityInfoListConverter;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bakery extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private String address;

	private String detailedAddress;

	private String hours;

	private String phoneNumber;
	private String checkPoint;
	private String newBreadTime;

	@Embedded
	private BakeryURL bakeryURL;

	@BatchSize(size = 2)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "bakery_images",
		joinColumns = @JoinColumn(name = "bakery_id")
	)
	private List<String> images = new ArrayList<>();

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BakeryStatus status;

	@OneToMany(mappedBy = "bakery", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> productList = new ArrayList<>();

	@Convert(converter = FacilityInfoListConverter.class)
	private List<FacilityInfo> facilityInfoList = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "report_id")
	private BakeryAddReport bakeryAddReport;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;

	@Builder
	public Bakery(
		Long id, String name, Double latitude, Double longitude,
		String address, String detailedAddress, String hours, String phoneNumber, String checkPoint,
		String newBreadTime, List<String> images,
		String websiteURL, String instagramURL, String facebookURL, String blogURL,
		List<FacilityInfo> facilityInfoList, BakeryStatus status, BakeryAddReport bakeryAddReport
	) {

		validateOnImagesCount(images);

		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.checkPoint = checkPoint;
		this.newBreadTime = newBreadTime;
		this.detailedAddress = detailedAddress;
		this.hours = hours;
		this.phoneNumber = phoneNumber;
		this.images = images;
		this.bakeryURL = BakeryURL.builder()
			.websiteURL(websiteURL).instagramURL(instagramURL).facebookURL(facebookURL).blogURL(blogURL).build();
		this.facilityInfoList = facilityInfoList;
		this.status = status;
		this.bakeryAddReport = bakeryAddReport;
	}

	public void update(
		String name, String address, String detailedAddress, Double latitude, Double longitude, String hours,
		String websiteURL, String instagramURL, String facebookURL, String blogURL, String phoneNumber,
		String checkPoint,
		String newBreadTime, List<String> updateImages,
		List<FacilityInfo> facilityInfoList, BakeryStatus status
	) {
		this.name = name;
		this.address = address;
		this.detailedAddress = detailedAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.hours = hours;
		this.phoneNumber = phoneNumber;
		updateImages(updateImages);
		this.bakeryURL.update(websiteURL, instagramURL, facebookURL, blogURL);
		this.facilityInfoList = facilityInfoList;
		this.status = status;
		this.checkPoint = checkPoint;
		this.newBreadTime = newBreadTime;
	}

	public void updateImages(List<String> updateImages) {
		if (this.images == null) {
			this.images = new ArrayList<>();
		}

		validateOnImagesCount(updateImages);

		this.images.clear();
		this.images.addAll(updateImages);
	}

	private void validateOnImagesCount(List<String> requestImages) {
		if (requestImages.size() > 2) {
			throw new DaedongException(DaedongStatus.BAKERY_IMAGE_CANNOT_MORE_THAN_TWO);
		}
	}

	public boolean isPosting() {
		return this.status.equals(BakeryStatus.POSTING);
	}

	public String getFullAddress() {
		return this.address + " " + (StringUtils.hasText(this.detailedAddress) ? this.detailedAddress : "");
	}

	double getDistanceFromUser(final Double userLatitude, final Double userLongitude) {
		final int earthRadius = 6371000;
		return floor(acos(cos(toRadians(userLatitude))
			* cos(toRadians(getLatitude()))
			* cos(toRadians(getLongitude()) - toRadians(userLongitude))
			+ sin(toRadians(userLatitude)) * sin(toRadians(getLatitude()))) * earthRadius);
	}

	public Double bakeryRating(List<Review> reviewList) {
		return Math.floor(reviewList.stream().map(Review::getAverageRating).toList()
			.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 10) / 10.0;
	}

	public User getPioneer() {
		return this.bakeryAddReport != null ? this.bakeryAddReport.getUser() : null;
	}

	public String getShortAddress() {
		final String[] addressSplit = this.address.split("\\s");
		return addressSplit.length == 1 ? addressSplit[0] : addressSplit[0] + " " + addressSplit[1];
	}

	public Product getProduct(Long productId) {
		return this.productList.stream()
			.filter(product -> Objects.equals(product.getId(), productId))
			.findAny()
			.orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Bakery bakery = (Bakery)o;
		return Objects.equals(id, bakery.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
