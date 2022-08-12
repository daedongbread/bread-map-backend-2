package com.depromeet.breadmapbackend.web.controller.admin.dto;

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
public class UpdateBakeryRequest {
    private Long bakeryId;
    private String name;
    private String address;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;
    private List<FacilityInfo> facilityInfoList;
    private List<UpdateBreadRequest> breadList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateBreadRequest {
        private Long breadId;
        private String name;
        private Integer price;
    }
}
