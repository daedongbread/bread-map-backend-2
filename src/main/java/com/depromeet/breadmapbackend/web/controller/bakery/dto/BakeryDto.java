package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
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
    private BakeryInfo bakeryInfo;
    private FlagInfo flagInfo;
    private List<FacilityInfo> facilityInfoList;

    @Getter
    @NoArgsConstructor
    public static class BakeryInfo {
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
            this.websiteURL = bakery.getBakeryURL().getWebsiteURL();
            this.instagramURL = bakery.getBakeryURL().getInstagramURL();
            this.facebookURL = bakery.getBakeryURL().getFacebookURL();
            this.blogURL = bakery.getBakeryURL().getBlogURL();
            this.phoneNumber = bakery.getPhoneNumber();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FlagInfo {
        private Long flagId;
        private Boolean isFlaged;

        @Builder
        public FlagInfo(FlagBakery flagBakery) {
            this.flagId = flagBakery != null ? flagBakery.getFlag().getId() : null;
            this.isFlaged = flagBakery != null;
        }
    }
}
