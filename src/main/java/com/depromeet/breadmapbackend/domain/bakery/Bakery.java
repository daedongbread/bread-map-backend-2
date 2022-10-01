package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.converter.FacilityInfoListConverter;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bakery extends BaseEntity {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String address;

    private String hours;

    private String phoneNumber;

    private String websiteURL;

    private String instagramURL;

    private String facebookURL;

    private String blogURL;

    private String image;

    private Integer flagNum;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BakeryStatus status;

    @OneToMany(mappedBy = "bakery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bread> breadList = new ArrayList<>();

    @OneToMany(mappedBy = "bakery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @Convert(converter = FacilityInfoListConverter.class)
    private List<FacilityInfo> facilityInfoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer views;

    @Builder
    public Bakery(Long id, String name, Double latitude, Double longitude,
                  String address, String hours, String phoneNumber,
                  String websiteURL, String instagramURL, String facebookURL, String blogURL,
                  String image, User user, List<FacilityInfo> facilityInfoList, BakeryStatus status) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.hours = hours;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.instagramURL = instagramURL;
        this.facebookURL = facebookURL;
        this.blogURL = blogURL;
        this.image = image;
        this.flagNum = 0;
        this.user = user;
        this.facilityInfoList = facilityInfoList;
        this.status = status;
        this.views = 0;
    }

    public void addFlagNum() {
        this.flagNum += 1;
    }

    public void minusFlagNum() {
        this.flagNum -= 1;
    }

    public void addFacilityInfo(FacilityInfo info) { this.facilityInfoList.add(info); }

    public void removeFacilityInfo(FacilityInfo info) { this.facilityInfoList.remove(info); }

    public void updateImage(String image) { this.image = image; }

    public void update(Long id, String name, String address, Double latitude, Double longitude, String hours,
                       String websiteURL, String instagramURL, String facebookURL, String blogURL, String phoneNumber,
                       List<FacilityInfo> facilityInfoList, BakeryStatus status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hours = hours;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.instagramURL = instagramURL;
        this.facebookURL = facebookURL;
        this.blogURL = blogURL;
        this.facilityInfoList = facilityInfoList;
        this.status = status;
    }

    public void addViews() { this.views += 1; }
}