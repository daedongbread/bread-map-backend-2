package com.depromeet.breadmapbackend.domain.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryDto {
    private BakeryInfo bakeryInfo;
    private PioneerInfo pioneerInfo;
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
        public BakeryInfo(Bakery bakery, Integer flagNum, Double rating, Integer reviewNum) {
            this.image = bakery.getImage();
            this.name = bakery.getName();
            this.flagNum = flagNum;
            this.rating = rating;
            this.reviewNum = reviewNum;
            this.address = bakery.getFullAddress();
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

    @Getter
    @NoArgsConstructor
    public static class PioneerInfo {
        private Long pioneerId;
        private String pioneerNickName;

        @Builder
        public PioneerInfo(User pioneer) {
            this.pioneerId = pioneer == null ? null : pioneer.getId();
            this.pioneerNickName = pioneer == null ? null : pioneer.getNickName();
        }
    }
}
