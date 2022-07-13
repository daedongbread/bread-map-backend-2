package com.depromeet.breadmapbackend.web.controller.admin.dto;

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
    private Long id;
    private String name;
    private String imageList;
    private String streetAddress;
    private String domicileAddress;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;
    private List<FacilityInfo> facilityInfoList;
    private List<AddBreadRequest> breadList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddBreadRequest {
        private String name;
        private String imageList;
        private Integer price;
    }
}
