package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewQueryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewService;
import com.depromeet.breadmapbackend.domain.search.dto.*;
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
    private final ReviewQueryRepository reviewQueryRepository;
    private final FlagBakeryRepository flagBakeryRepository;

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
    public SearchResultResponse searchEngine(String oAuthId, String keyword, Double userLat, Double userLng, SearchType searchType) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        searchLogService.saveRecentSearchLog(oAuthId, keyword);

        SearchResultResponse.SearchResultResponseBuilder builder = SearchResultResponse.builder();

        keyword = checkEndingWithStation(keyword);

        List<SubwayStation> subwayStationList = subwayStationRepository.findByName(keyword);
        SearchResponse document;

        if (!subwayStationList.isEmpty()) {
            document = openSearchService.getDocumentByGeology(keyword, subwayStationList.get(0).getLatitude(), subwayStationList.get(0).getLongitude());
            builder.subwayStationName(keyword.concat("역"));
        } else {
            document = openSearchService.getBreadByKeyword(keyword);
        }

        List<SearchEngineDto> searchResults = new ArrayList<>(getSearchEngineDtoList(document.getHits(), userLat, userLng));

        if (searchResults.size() < 7) {
            document = openSearchService.getBakeryByKeyword(keyword);
            searchResults.addAll(getSearchEngineDtoList(document.getHits(), userLat, userLng));
        }

        List<Long> bakeryIds = SearchEngineDto.extractBakeryIdList(searchResults);
        List<BakeryReviewScoreDto> bakeriesReviews = reviewQueryRepository.getBakeriesReview(bakeryIds);

        List<SearchResultDto> searchResultDtos = this.mergeSearchEngineAndAdditionalInfo(searchResults, user.getId(), bakeriesReviews);

        // 인기순/거리순 정렬
        resultSortBySearchType(searchType, searchResultDtos);

        // keyword 위치 정렬
        String finalKeyword = keyword;
        searchResultDtos.sort((dto1, dto2) -> {
            int index1 = dto1.getBakeryName().indexOf(finalKeyword);
            int index2 = dto2.getBakeryName().indexOf(finalKeyword);

            if (index1 < 0) {
                return 1;
            } else if (index2 < 0) {
                return -1;
            } else {
                return Integer.compare(index2, index1);
            }
        });

        return builder
                .searchResultDtoList(searchResultDtos)
                .build();
    }

    private List<SearchResultDto> mergeSearchEngineAndAdditionalInfo(List<SearchEngineDto> searchResults, Long userId, List<BakeryReviewScoreDto> bakeriesReviews) {

        List<SearchResultDto> searchResultDtos = new ArrayList<>();

        for (SearchEngineDto searchResultDto : searchResults) {
            SearchResultDto.SearchResultDtoBuilder searchResultDtoBuilder = SearchResultDto.builder();

            Optional<Bakery> bakeryOptional = bakeryRepository.findById(searchResultDto.getBakeryId());
            if(bakeryOptional.isPresent()) {
                searchResultDtoBuilder
                        .latitude(bakeryOptional.get().getLatitude())
                        .longitude(bakeryOptional.get().getLongitude())
                        .bakeryImageUrl(bakeryOptional.get().getImages());

                searchResultDtoBuilder.flagCount(flagBakeryRepository.countFlagNum(bakeryOptional.get()));

                Optional<FlagBakery> flagBakeryOptional = flagBakeryRepository.findByBakeryAndUserId(bakeryOptional.get(), userId);
                if(flagBakeryOptional.isPresent()) {
                    searchResultDtoBuilder.flagColor(flagBakeryOptional.get().getFlag().getColor().getCode());
                }
            }

            searchResultDtoBuilder
                    .bakeryId(searchResultDto.getBakeryId())
                    .bakeryName(searchResultDto.getBakeryName())
                    .address(searchResultDto.getAddress())
                    .distance(searchResultDto.getDistance())
                    .reviewNum(0L) // init
                    .totalScore(0d); // init

            if (searchResultDto.getBreadId() != null) {
                searchResultDtoBuilder
                        .breadId(searchResultDto.getBreadId())
                        .breadName(searchResultDto.getBreadName());
            }

            for (BakeryReviewScoreDto bakeryReviewScoreDto : bakeriesReviews) {
                if (searchResultDto.getBakeryId().equals(bakeryReviewScoreDto.getBakeryId())) {
                    searchResultDtoBuilder.reviewNum(bakeryReviewScoreDto.getReviewCount());
                    searchResultDtoBuilder.totalScore(bakeryReviewScoreDto.getTotalScore());
                }
            }
            searchResultDtos.add(searchResultDtoBuilder.build());
        }

        return searchResultDtos;
    }

    private static void resultSortBySearchType(SearchType searchType, List<SearchResultDto> searchResults) {
        if (searchType == SearchType.DISTANCE) {
            searchResults.sort(Comparator.comparingDouble(SearchResultDto::getDistance));
        } else {
            searchResults.sort((dto1, dto2) -> Double.compare(dto2.getReviewNum(), dto1.getReviewNum()));
        }
    }

    private String checkEndingWithStation(String keyword) {
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
                        + sin(toRadians(userLat)) * sin(toRadians(locationLat))) * 6371000));


        if (sourceAsMap.get("breadId") != null) {
            searchEngineDtoBuilder
                    .breadId(Long.valueOf((Integer) sourceAsMap.get("bakeryId")))
                    .breadName((String) sourceAsMap.get("breadName"));
        }

        return searchEngineDtoBuilder;
    }

    @Override
    public List<String> searchKeywordSuggestions(String keyword) {

        SearchResponse bakerySuggestions = openSearchService.getKeywordSuggestions(OpenSearchIndex.BAKERY_SEARCH, keyword);
        HashSet<String> keywordSuggestions = Arrays.stream(bakerySuggestions.getHits().getHits())
                .map(breadHits -> (String) breadHits.getSourceAsMap().get("bakeryName"))
                .collect(Collectors.toCollection(HashSet::new));

        if (keywordSuggestions.size() < MAX_KEYWORD_SUGGESTION) {
            SearchResponse breadSuggestions = openSearchService.getKeywordSuggestions(OpenSearchIndex.BREAD_SEARCH, keyword);
            for (SearchHit breadHit : breadSuggestions.getHits().getHits()) {
                keywordSuggestions.add((String) breadHit.getSourceAsMap().get("breadName"));
            }
        }

        List<String> sortedResult = new ArrayList<>(keywordSuggestions);
        sortedResult.sort((s1, s2) -> {
            int index1 = s1.indexOf(keyword);
            int index2 = s2.indexOf(keyword);

            if (index1 < 0) {
                return 1;
            } else if (index2 < 0) {
                return -1;
            } else {
                return Integer.compare(index2, index1);
            }
        });

        if(sortedResult.size() > 1) {
            sortedResult.add(0, keyword);
        }

        return sortedResult;
    }

    private Double bakeryRating(List<Review> reviewList) {
        return Math.floor(reviewList.stream().map(Review::getAverageRating).toList()
                .stream().mapToDouble(Double::doubleValue).average().orElse(0) * 10) / 10.0;
    }

}
