package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import lombok.*;

@Getter
@NoArgsConstructor
public class BakeryInfo {
    private String image;
    private String name;
    private Integer flagNum;
    private Double rating;
    private Integer reviewNum;

    private String address;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;

    @Builder
    public BakeryInfo(Bakery bakery, Double rating, Integer reviewNum) {
        this.image = bakery.getImage();
        this.name = bakery.getName();
        this.flagNum = bakery.getFlagNum();
        this.rating = rating;
        this.reviewNum = reviewNum;
        this.address = bakery.getAddress();
        this.hours = bakery.getHours();
        this.websiteURL = bakery.getWebsiteURL();
        this.instagramURL = bakery.getInstagramURL();
        this.facebookURL = bakery.getFacebookURL();
        this.blogURL = bakery.getBlogURL();
        this.phoneNumber = bakery.getPhoneNumber();
    }
}
