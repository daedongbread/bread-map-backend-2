package com.depromeet.breadmapbackend.service.search;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.search.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private String REDIS_KEY_RECENT = "recentKeywords:";

    @Transactional(readOnly = true)
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
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SearchDto search(String username, String word, Double latitude, Double longitude) {
        if(userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException();

        ZSetOperations<String, String> redisRecentSearch = redisTemplate.opsForZSet();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS")); // timestamp로!!
        redisRecentSearch.add(REDIS_KEY_RECENT + username, word, Double.parseDouble(time));
        redisRecentSearch.removeRange(REDIS_KEY_RECENT + username, -(10 + 1), -(10 + 1));

        Bakery bakery = bakeryRepository.findByName(word).orElseThrow(BakeryNotFoundException::new);
        return SearchDto.builder()
                .bakeryId(bakery.getId()).bakeryName(bakery.getName())
                .reviewNum(bakery.getReviewList().size())
                .distance(floor(acos(cos(toRadians(latitude))
                        * cos(toRadians(bakery.getLatitude()))
                        * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                        + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build();
    }

    @Transactional(readOnly = true)
    public List<String> recentKeywords(String username) {
        if(userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException();
        return new ArrayList<>(Objects.requireNonNull(
                redisTemplate.opsForZSet().reverseRange(REDIS_KEY_RECENT + username, 0, -1)));
    }

    @Transactional
    public void deleteRecentKeyword(String username, String keyword) {
        if(userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException();
        redisTemplate.opsForZSet().remove(REDIS_KEY_RECENT + username, keyword);
    }

    @Transactional
    public void deleteRecentKeywordAll(String username) {
        if(userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException();
        redisTemplate.delete(REDIS_KEY_RECENT + username);
    }
}
