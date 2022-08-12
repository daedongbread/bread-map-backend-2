package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminBakeryDto {
    private String name;
    private String image;

    private String address;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;

    private List<AdminBreadDto> menu;

    @Builder
    public AdminBakeryDto(Bakery bakery, List<AdminBreadDto> menu) {
        this.name = bakery.getName();
        this.image = bakery.getImage();
        this.address = bakery.getAddress();
        this.hours = bakery.getHours();
        this.websiteURL = bakery.getWebsiteURL();
        this.instagramURL = bakery.getInstagramURL();
        this.facebookURL = bakery.getFacebookURL();
        this.blogURL = bakery.getBlogURL();
        this.phoneNumber = bakery.getPhoneNumber();
        this.menu = menu;
    }
}
