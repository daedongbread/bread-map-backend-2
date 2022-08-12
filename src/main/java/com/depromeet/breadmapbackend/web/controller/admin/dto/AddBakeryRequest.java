package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBakeryRequest {
    private Long bakeryId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;
    private List<FacilityInfo> facilityInfoList;
    private List<AddBreadRequest> breadList;
    private BakeryStatus status;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddBreadRequest {
        private String name;
        private Integer price;
    }
}
