package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.Hours;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Created by ParkSuHo by 2022/03/27.
 * Update by ChoiHyunWoo by 2022/05/15.
 */
@Getter
@NoArgsConstructor
public class BakeryInfo {
    private String image;
    private String name;
    private Integer flagNum;
    private Double rating;
    private Integer reviewNum;

    private String domicileAddress;
    private String streetAddress;
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
        this.domicileAddress = bakery.getDomicileAddress();
        this.streetAddress = bakery.getStreetAddress();
        this.hours = bakery.getHours();
        this.websiteURL = bakery.getWebsiteURL();
        this.instagramURL = bakery.getInstagramURL();
        this.facebookURL = bakery.getFacebookURL();
        this.blogURL = bakery.getBlogURL();
        this.phoneNumber = bakery.getPhoneNumber();
    }
}
