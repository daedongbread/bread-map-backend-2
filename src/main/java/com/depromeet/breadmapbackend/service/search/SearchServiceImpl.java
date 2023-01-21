package com.depromeet.breadmapbackend.service.search;

import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.web.controller.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
        return bakeryRepository.findByNameStartsWith(word).stream()
                .map(bakery -> SearchDto.builder()
                        .bakeryId(bakery.getId()).bakeryName(bakery.getName())
                        .reviewNum(bakery.getReviewList().size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .sorted(Comparator.comparing(SearchDto::getDistance))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SearchDto> search(String username, String word, Double latitude, Double longitude) {
        if(userRepository.findByUsername(username).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);

        ZSetOperations<String, String> redisRecentSearch = redisTemplate.opsForZSet();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")); // timestamp로!!
        redisRecentSearch.add(customRedisProperties.getKey().getRecent() + ":" + username, word, Double.parseDouble(time));
        redisRecentSearch.removeRange(customRedisProperties.getKey().getRecent() + ":" + username, -(10 + 1), -(10 + 1));

        return bakeryRepository.findByNameStartsWith(word).stream()
                .map(bakery -> SearchDto.builder()
                        .bakeryId(bakery.getId()).bakeryName(bakery.getName())
                        .reviewNum(bakery.getReviewList().size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .sorted(Comparator.comparing(SearchDto::getDistance))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<String> recentKeywords(String username) {
        if(userRepository.findByUsername(username).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        return new ArrayList<>(Objects.requireNonNull(
                redisTemplate.opsForZSet().reverseRange(customRedisProperties.getKey().getRecent() + ":" + username, 0, -1)));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRecentKeyword(String username, String keyword) {
        if(userRepository.findByUsername(username).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        redisTemplate.opsForZSet().remove(customRedisProperties.getKey().getRecent() + ":" + username, keyword);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRecentKeywordAll(String username) {
        if(userRepository.findByUsername(username).isEmpty()) throw new DaedongException(DaedongStatus.USER_NOT_FOUND);
        redisTemplate.delete(customRedisProperties.getKey().getRecent() + ":" + username);
    }
}
