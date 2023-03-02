package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryURL {
    private String websiteURL;
    private String instagramURL;
    private String facebookURL;
    private String blogURL;

    public void update(String websiteURL, String instagramURL, String facebookURL, String blogURL) {
        this.websiteURL = websiteURL;
        this.instagramURL = instagramURL;
        this.facebookURL = facebookURL;
        this.blogURL = blogURL;
    }
}
