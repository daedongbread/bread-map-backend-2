package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/*
 * Created by ParkSuHo by 2022/03/27.
 */
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class BakeryDto {
    private final String name;
    private final Integer flagNum;
    private final Long rating;
    private final Integer reviewNum;

    private final String streetAddress;
    private final String hour;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private final String phoneNumber;
}
