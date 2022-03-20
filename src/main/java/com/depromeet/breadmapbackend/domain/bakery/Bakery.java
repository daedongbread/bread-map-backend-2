package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.AmenityTypeListConverter;
import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.common.StringListConverter;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bakery extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String address;

    private String businessHour;

    private String telNumber;

    @Convert(converter = StringListConverter.class)
    private List<String> websiteUrlList = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    private List<String> imgPathList = new ArrayList<>();

    @Convert(converter = AmenityTypeListConverter.class)
    private List<AmenityType> amenityTypeList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User host; // 빵집 등록자

    @Builder
    private Bakery(String name, Double latitude, Double longitude, String address, String businessHour, String telNumber, List<String> websiteUrlList, List<String> imgPathList, List<AmenityType> amenityTypeList, User host) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.businessHour = businessHour;
        this.telNumber = telNumber;
        this.websiteUrlList = websiteUrlList;
        this.imgPathList = imgPathList;
        this.amenityTypeList = amenityTypeList;
        this.host = host;
    }

}