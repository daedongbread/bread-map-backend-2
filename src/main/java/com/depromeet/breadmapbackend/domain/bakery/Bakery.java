package com.depromeet.breadmapbackend.domain.bakery;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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

import org.springframework.util.StringUtils;

import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.FacilityInfoListConverter;

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

	@Embedded
	private BakeryURL bakeryURL;

	private String image;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BakeryStatus status;

	@OneToMany(mappedBy = "bakery", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> productList = new ArrayList<>();

	@Convert(converter = FacilityInfoListConverter.class)
	private List<FacilityInfo> facilityInfoList = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pioneer_id")
	private User pioneer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;

	@Builder
	public Bakery(
		String name, Double latitude, Double longitude,
		String address, String detailedAddress, String hours, String phoneNumber,
		String websiteURL, String instagramURL, String facebookURL, String blogURL, String image,
		List<FacilityInfo> facilityInfoList, BakeryStatus status, User pioneer
	) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.detailedAddress = detailedAddress;
		this.hours = hours;
		this.phoneNumber = phoneNumber;
		this.bakeryURL = BakeryURL.builder()
			.websiteURL(websiteURL).instagramURL(instagramURL).facebookURL(facebookURL).blogURL(blogURL).build();
		this.image = image;
		this.facilityInfoList = facilityInfoList;
		this.status = status;
		this.pioneer = pioneer;
	}

	public void update(
		String name, String address, String detailedAddress, Double latitude, Double longitude, String hours,
		String websiteURL, String instagramURL, String facebookURL, String blogURL, String phoneNumber, String image,
		List<FacilityInfo> facilityInfoList, BakeryStatus status
	) {
		this.name = name;
		this.address = address;
		this.detailedAddress = detailedAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.hours = hours;
		this.phoneNumber = phoneNumber;
		this.image = image;
		this.bakeryURL.update(websiteURL, instagramURL, facebookURL, blogURL);
		this.facilityInfoList = facilityInfoList;
		this.status = status;
	}

	public void updatePioneer(User pioneer) {
		this.pioneer = pioneer;
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
}