package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchEngineDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.search.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
    private final ReviewService reviewService;
    private final SearchLogService searchLogService;
    private final OpenSearchService openSearchService;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SearchDto> searchDatabase(String oAuthId, String word, Double latitude, Double longitude) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        searchLogService.saveRecentSearchLog(me.getId(), word);

        return bakeryRepository.find10ByNameContainsIgnoreCaseAndStatusOrderByDistance(word.strip(), word.replaceAll(" ", ""), latitude, longitude, 10).stream()
                .map(bakery -> SearchDto.builder()
                        .bakery(bakery)
                        .rating(bakeryRating(reviewService.getReviewList(me, bakery)))
                        .reviewNum(reviewService.getReviewList(me, bakery).size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude())- toRadians(longitude))
                                + sin(toRadians(latitude))*sin(toRadians(bakery.getLatitude())))*6371000)).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchEngineDto> searchEngine(String oAuthId, String keyword, Double userLat, Double userLng) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        searchLogService.saveRecentSearchLog(me.getId(), keyword);

        SearchResponse document = openSearchService.getDocumentByKeyword(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), keyword);
        List<SearchHit> searchHits = Arrays.stream(document.getHits().getHits()).toList();

        List<SearchEngineDto> list = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            double locationLat = Double.parseDouble((String) sourceAsMap.get("latitude"));
            double locationLng = Double.parseDouble((String) sourceAsMap.get("longitude"));

            SearchEngineDto build = SearchEngineDto.builder()
                    .breadId(Long.parseLong((String) sourceAsMap.get("breadId")))
                    .breadName((String) sourceAsMap.get("breadName"))
                    .bakeryId(Long.parseLong((String) sourceAsMap.get("bakeryId")))
                    .bakeryName((String) sourceAsMap.get("bakeryName"))
                    .address((String) sourceAsMap.get("bakeryAddress"))
                    .distance(floor(acos(cos(toRadians(userLat))
                            * cos(toRadians(locationLat))
                            * cos(toRadians(locationLng))) - toRadians(userLng)
                            + sin(toRadians(userLat))*sin(toRadians(locationLat)))*6371000)
                    .rating(Double.valueOf((String) sourceAsMap.get("totalScore")))
                    .reviewNum(Integer.valueOf((String) sourceAsMap.get("reviewCount")))
                    .build();
            list.add(build);
        }
        return list;
    }

    private Double bakeryRating(List<Review> reviewList) { // TODO
        return Math.floor(reviewList.stream().map(Review::getAverageRating).toList()
                .stream().mapToDouble(Double::doubleValue).average().orElse(0)*10)/10.0;
    }

}
