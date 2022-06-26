package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBakeryDto {
    private String name;
    private String image;

    private String domicileAddress;
    private String streetAddress;
    private String hours;
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;
    private String phoneNumber;

    @Builder
    public AdminBakeryDto(Bakery bakery) {
        this.name = bakery.getName();
        this.image = bakery.getImage();

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
