package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryDto {
    private BakeryInfo info;
    private List<ProductDto> menu;
    private List<FacilityInfo> facilityInfoList;

    @Getter
    @NoArgsConstructor
    public static class BakeryInfo {
        private String image;
        private String name;
        private Integer flagNum;
        private Double rating;
        private Integer reviewNum;

        private Boolean isFlaged;

        private String address;
        private String hours;
        private String websiteURL;
        private String instagramURL;
        private String facebookURL;
        private String blogURL;
        private String phoneNumber;

        @Builder
        public BakeryInfo(Bakery bakery, Boolean isFlaged, Double rating, Integer reviewNum) {
            this.image = bakery.getImage();
            this.name = bakery.getName();
            this.flagNum = bakery.getFlagNum();
            this.rating = rating;
            this.reviewNum = reviewNum;
            this.isFlaged = isFlaged;
            this.address = bakery.getAddress();
            this.hours = bakery.getHours();
            this.websiteURL = bakery.getWebsiteURL();
            this.instagramURL = bakery.getInstagramURL();
            this.facebookURL = bakery.getFacebookURL();
            this.blogURL = bakery.getBlogURL();
            this.phoneNumber = bakery.getPhoneNumber();
        }
    }
}
