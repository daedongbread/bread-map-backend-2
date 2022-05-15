package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import lombok.*;

import java.util.List;

/*
 * Created by ParkSuHo by 2022/03/27.
 * Update by ChoiHyunWoo by 2022/05/15.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryDto {
    private String name;
    private Double latitude;
    private Double longitude;

    private String domicileAddress;
    private String phoneNumber;
    private String streetAddress;
    private String websiteURL;
}
