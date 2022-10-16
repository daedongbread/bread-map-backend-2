package com.depromeet.breadmapbackend.infra.feign.client;

import com.depromeet.breadmapbackend.infra.feign.config.SgisClientConfig;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisTokenDto;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisTranscoordDto;
import com.depromeet.breadmapbackend.infra.feign.dto.SgisGeocodeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sgisClient", url = "https://sgisapi.kostat.go.kr/OpenAPI3", configuration = SgisClientConfig.class)
public interface SgisClient {
    @GetMapping("/auth/authentication.json")
    SgisTokenDto getToken(@RequestParam("consumer_key") String consumer_key, @RequestParam("consumer_secret") String consumer_secret);

    @GetMapping(value = "/addr/geocode.json")
    SgisGeocodeDto getGeocode(@RequestParam("accessToken") String accessToken, @RequestParam("address") String address);

    @GetMapping(value = "/transformation/transcoord.json")
    SgisTranscoordDto getTranscoord(@RequestParam("accessToken") String accessToken, @RequestParam("src") int src,
                                    @RequestParam("dst") int dst, @RequestParam("posX") String posX, @RequestParam("posY") String posY);
}
