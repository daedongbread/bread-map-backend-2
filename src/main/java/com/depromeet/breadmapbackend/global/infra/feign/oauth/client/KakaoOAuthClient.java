package com.depromeet.breadmapbackend.global.infra.feign.oauth.client;

import com.depromeet.breadmapbackend.global.infra.feign.config.FeignClientConfig;
import com.depromeet.breadmapbackend.global.infra.feign.oauth.dto.OIDCPublicKeysDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "KakaoOAuthClient", url = "https://kauth.kakao.com", configuration = FeignClientConfig.class)
public interface KakaoOAuthClient {
    @Cacheable(cacheNames = "KakaoOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeysDto getOIDCPublicKeys();

    @CacheEvict(cacheNames = "KakaoOICD", allEntries = true)
    void clearCache();
}
