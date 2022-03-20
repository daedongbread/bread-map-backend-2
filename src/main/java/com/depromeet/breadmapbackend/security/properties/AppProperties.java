package com.depromeet.breadmapbackend.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "auth")
public class AppProperties {

    private String tokenSecret;
    private long accessTokenExpiry;
    private long refreshTokenExpiry;

}
