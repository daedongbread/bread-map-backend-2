package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.FacilityInfoListConverter;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
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
    private String streetAddress;

    @Column(nullable = false)
    private String domicileAddress;

    private String hour;

    private String phoneNumber;

    private String websiteURL;

    private String instagramURL;

    private String facebookURL;

    private String blogURL;

    private String image;

    private Long rating;

    @Convert(converter = FacilityInfoListConverter.class)
    private List<FacilityInfo> facilityInfoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Bakery(Long id, String name, Double latitude, Double longitude,
                   String streetAddress, String domicileAddress, String hour, String phoneNumber,
                   String websiteURL, String instagramURL, String facebookURL, String blogURL,
                   String image, Long rating, List<FacilityInfo> facilityInfoList, User user) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streetAddress = streetAddress;
        this.domicileAddress = domicileAddress;
        this.hour = hour;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.instagramURL = instagramURL;
        this.facebookURL = facebookURL;
        this.blogURL = blogURL;
        this.image = image;
        this.rating = rating;
        this.facilityInfoList = facilityInfoList;
        this.user = user;
    }
}