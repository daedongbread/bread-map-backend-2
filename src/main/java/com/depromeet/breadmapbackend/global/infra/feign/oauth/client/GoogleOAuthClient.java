package com.depromeet.breadmapbackend.global.infra.feign.oauth.client;

import com.depromeet.breadmapbackend.global.infra.feign.config.FeignClientConfig;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GoogleOAuthClient", url = "https://www.googleapis.com", configuration = FeignClientConfig.class)
public interface GoogleOAuthClient {
    @Cacheable(cacheNames = "GoogleOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/oauth2/v3/certs")
    OIDCPublicKeysDto getOIDCPublicKeys();
}
