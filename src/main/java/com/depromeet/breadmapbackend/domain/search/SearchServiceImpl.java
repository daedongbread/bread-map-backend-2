package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.SearchDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchEngineDto;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.SearchResultResponse;
import com.depromeet.breadmapbackend.domain.subway.SubwayStation;
import com.depromeet.breadmapbackend.domain.subway.SubwayStationRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final static Integer MAX_KEYWORD_SUGGESTION = 12;

    private final BakeryRepository bakeryRepository;
    private final UserRepository userRepository;
    private final SubwayStationRepository subwayStationRepository;

    private final ReviewService reviewService;
    private final SearchLogService searchLogService;
    private final OpenSearchService openSearchService;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SearchDto> searchDatabase(String oAuthId, String keyword, Double latitude, Double longitude) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        searchLogService.saveRecentSearchLog(oAuthId, keyword);

        return bakeryRepository.find10ByNameContainsIgnoreCaseAndStatusOrderByDistance(keyword.strip(), keyword.replaceAll(" ", ""), latitude, longitude, 10).stream()
                .map(bakery -> SearchDto.builder()
                        .bakery(bakery)
                        .rating(bakeryRating(reviewService.getReviewList(me, bakery)))
                        .reviewNum(reviewService.getReviewList(me, bakery).size())
                        .distance(floor(acos(cos(toRadians(latitude))
                                * cos(toRadians(bakery.getLatitude()))
                                * cos(toRadians(bakery.getLongitude()) - toRadians(longitude))
                                + sin(toRadians(latitude)) * sin(toRadians(bakery.getLatitude()))) * 6371000)).build())
                .collect(Collectors.toList());
    }

    @Override
    public SearchResultResponse searchEngine(String oAuthId, String keyword, Double userLat, Double userLng) {
        userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        searchLogService.saveRecentSearchLog(oAuthId, keyword);

        SearchResultResponse.SearchResultResponseBuilder builder = SearchResultResponse.builder();

        keyword = processKeyword(keyword);

        List<SubwayStation> subwayStationList = subwayStationRepository.findByName(keyword);
        SearchResponse document;
        List<SearchEngineDto> searchResults = new ArrayList<>();

        if (!subwayStationList.isEmpty()) {
            document = openSearchService.getDocumentByGeology(keyword, subwayStationList.get(0).getLatitude(), subwayStationList.get(0).getLongitude());
            searchResults.addAll(getSearchEngineDtoList(document.getHits(), userLat, userLng));
            builder.subwayStationName(keyword.concat("역"));
        } else {
            document = openSearchService.getBreadByKeyword(keyword);
            searchResults.addAll(getSearchEngineDtoList(document.getHits(), userLat, userLng));
        }

        if (searchResults.size() < 7) {
            document = openSearchService.getBakeryByKeyword(keyword);
            searchResults.addAll(getSearchEngineDtoList(document.getHits(), userLat, userLng));
        }

        return builder
                .searchEngineDtoList(searchResults)
                .build();
    }


    private String processKeyword(String keyword) {
        if (keyword.endsWith("역")) {
            return keyword.substring(0, keyword.length() - 1);
        }
        return keyword;
    }

    private List<SearchEngineDto> getSearchEngineDtoList(SearchHits hits, Double userLat, Double userLng) {
        return Arrays.stream(hits.getHits())
                .map(searchHit -> getSearchEngineDtoBuilder(userLat, userLng, searchHit).build())
                .collect(Collectors.toList());
    }

    private static SearchEngineDto.SearchEngineDtoBuilder getSearchEngineDtoBuilder(Double userLat, Double userLng, SearchHit searchHit) {
        Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
        double locationLat = Double.parseDouble((String) sourceAsMap.get("latitude"));
        double locationLng = Double.parseDouble((String) sourceAsMap.get("longitude"));

        SearchEngineDto.SearchEngineDtoBuilder searchEngineDtoBuilder = SearchEngineDto.builder()
                .bakeryId(Long.valueOf((Integer) sourceAsMap.get("bakeryId")))
                .bakeryName((String) sourceAsMap.get("bakeryName"))
                .address((String) sourceAsMap.get("bakeryAddress"))
                .distance(floor(acos(cos(toRadians(userLat))
                        * cos(toRadians(locationLat))
                        * cos(toRadians(locationLng) - toRadians(userLng))
                        + sin(toRadians(userLat)) * sin(toRadians(locationLat))) * 6371000))
                .rating(!sourceAsMap.get("totalScore").equals("null") ? Double.valueOf((String) sourceAsMap.get("totalScore")) : null)
                .reviewNum(Integer.valueOf((String) sourceAsMap.get("reviewCount")));


        if (sourceAsMap.get("breadId") != null) {
            searchEngineDtoBuilder
                    .breadId(Long.valueOf((Integer) sourceAsMap.get("bakeryId")))
                    .breadName((String) sourceAsMap.get("breadName"));
        }

        return searchEngineDtoBuilder;
    }

    @Override
    public HashSet<String> searchKeywordSuggestions(String word) {
        HashSet<String> keywordSuggestions;
        SearchResponse bakerySuggestions = openSearchService.getKeywordSuggestions(OpenSearchIndex.BAKERY_SEARCH, word);
        keywordSuggestions = Arrays.stream(bakerySuggestions.getHits().getHits())
                .map(breadHits -> (String) breadHits.getSourceAsMap().get("bakeryName"))
                .collect(Collectors.toCollection(HashSet::new));

        if (keywordSuggestions.size() < MAX_KEYWORD_SUGGESTION) {
            SearchResponse breadSuggestions = openSearchService.getKeywordSuggestions(OpenSearchIndex.BREAD_SEARCH, word);
            for (SearchHit breadHit : breadSuggestions.getHits().getHits()) {
                keywordSuggestions.add((String) breadHit.getSourceAsMap().get("breadName"));
            }
        }
        return keywordSuggestions;
    }

    private Double bakeryRating(List<Review> reviewList) {
        return Math.floor(reviewList.stream().map(Review::getAverageRating).toList()
                .stream().mapToDouble(Double::doubleValue).average().orElse(0) * 10) / 10.0;
    }

}
