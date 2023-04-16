package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final CustomRedisProperties customRedisProperties;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SearchDto> autoComplete(String word, Double latitude, Double longitude) {
        return bakeryRepository.findByNameContainsIgnoreCaseOrderByDistance(word, latitude, longitude, 10).stream()
                .map(bakery -> SearchDto.builder()
                        .bakeryId(bakery.getId()).bakeryName(bakery.getName())
                        .reviewNum(bakery.getReviewList().size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude()) - toRadians(longitude))
                                + sin(toRadians(latitude)) * sin(toRadians(bakery.getLatitude()))) * 6371000)).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SearchDto> search(String oAuthId, String word, Double latitude, Double longitude) {
        if(userRepository.findByOAuthId(oAuthId).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);

        ZSetOperations<String, String> redisRecentSearch = redisTemplate.opsForZSet();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")); // timestamp로!!
        redisRecentSearch.add(customRedisProperties.getKey().getRecent() + ":" + oAuthId, word, Double.parseDouble(time));
        redisRecentSearch.removeRange(customRedisProperties.getKey().getRecent() + ":" + oAuthId, -(10 + 1), -(10 + 1));

        return  bakeryRepository.findByNameContainsIgnoreCaseOrderByDistance(word, latitude, longitude, 10).stream()
                .map(bakery -> SearchDto.builder()
                        .bakeryId(bakery.getId()).bakeryName(bakery.getName())
                        .reviewNum(bakery.getReviewList().size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<String> recentKeywords(String oAuthId) {
        if(userRepository.findByOAuthId(oAuthId).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        return new ArrayList<>(Objects.requireNonNull(
                redisTemplate.opsForZSet().reverseRange(customRedisProperties.getKey().getRecent() + ":" + oAuthId, 0, -1)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRecentKeyword(String oAuthId, String keyword) {
        if(userRepository.findByOAuthId(oAuthId).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        redisTemplate.opsForZSet().remove(customRedisProperties.getKey().getRecent() + ":" + oAuthId, keyword);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRecentKeywordAll(String oAuthId) {
        if(userRepository.findByOAuthId(oAuthId).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        redisTemplate.delete(customRedisProperties.getKey().getRecent() + ":" + oAuthId);
    }
}
