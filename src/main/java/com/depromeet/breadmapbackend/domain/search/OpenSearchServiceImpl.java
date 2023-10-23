package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.bakery.BakeryQueryRepository;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.CommonLoadData;
import com.depromeet.breadmapbackend.domain.search.utils.UnicodeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.OpenSearchException;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchServiceImpl implements OpenSearchService {

    @Value("${cloud.aws.open-search.id}")
    private String openSearchId;
    @Value("${cloud.aws.open-search.password}")
    private String openSearchPassword;
    @Value("${cloud.aws.open-search.host}")
    private String openSearchHost;
    private final static Double LATITUDE_1KM = 1/109.958489;
    private final static Double LONGITUDE_1KM = 1/88.74;

    private final BakeryQueryRepository bakeryQueryRepository;

    @Override
    public OpenSearchCreateIndexResponse createIndex(String indexName) throws IOException {
        try (RestHighLevelClient searchClient = searchClient()) {
            //Create a non-default index with custom settings and mappings.
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

            createIndexRequest.settings(Settings.builder() //Specify in the settings how many shards you want in the index.
                    .put("index.number_of_shards", 4)
                    .put("index.number_of_replicas", 3)
            );
            //Create a set of maps for the index's mappings.
            HashMap<String, String> typeMapping = new HashMap<>();
            typeMapping.put("type", "integer");
            HashMap<String, Object> ageMapping = new HashMap<>();
            ageMapping.put("age", typeMapping);
            HashMap<String, Object> mapping = new HashMap<>();
            mapping.put("properties", ageMapping);
            createIndexRequest.mapping(mapping);
            searchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (OpenSearchStatusException osse) {
            return new OpenSearchCreateIndexResponse(osse.getMessage());

        }
        return new OpenSearchCreateIndexResponse(indexName + " :: 인덱스 생성에 성공했습니다.");
    }

    @Override
    public AcknowledgedResponse deleteIndex(String indexName) throws IOException {
        AcknowledgedResponse acknowledgedResponse = new AcknowledgedResponse(false);
        //Adding data to the index.
        try (RestHighLevelClient searchClient = searchClient()) {
            try {
                DeleteIndexRequest request = new DeleteIndexRequest(indexName); //Add a document to the custom-index we created.
                acknowledgedResponse = searchClient.indices().delete(request, RequestOptions.DEFAULT);
            } catch (OpenSearchException ose) {
                log.debug("deleteIndex :: " + ose.getDetailedMessage());
            }

            return acknowledgedResponse;
        }
    }

    @Override
    public IndexResponse addDataToIndex(String indexName, HashMap<String, String> stringMapping) throws IOException {
        IndexResponse response = null;
        //Adding data to the index.
        try (RestHighLevelClient searchClient = searchClient()) {
            IndexRequest request = new IndexRequest(indexName);

            if (indexName.equals(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion())) {
                request.id("bakery" + stringMapping.get("bakeryId"));
            } else if (indexName.equals(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion())) {
                request.id("bread" + stringMapping.get("breadId"));
            }

            request.source(stringMapping);

            try {
                response = searchClient.index(request, RequestOptions.DEFAULT);
            } catch (ConnectException ce) {
                log.debug("addDataToIndex :: " + ce.getMessage());
            }
            return response;
        }
    }

    @Override
    public SearchResponse getDocumentByKeyword(String indexName, String keyword) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery
                .should(QueryBuilders.matchQuery("bakeryName", keyword))
                .should(QueryBuilders.matchQuery("breadName", keyword))
                .should(QueryBuilders.matchQuery("chosung", keyword))
                .should(QueryBuilders.matchQuery("bakeryAddress", keyword));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(7)
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .query(boolQuery);

        searchSourceBuilder.query(boolQuery);

        SearchRequest searchRequest = new SearchRequest()
                .indices(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion())
                .source(searchSourceBuilder);

        SearchResponse response;
        try (RestHighLevelClient searchClient = searchClient()) {
            response = searchClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @Override
    public SearchResponse getDocumentByGeology(String indexName, Double latitude, Double longitude) {

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(QueryBuilders.rangeQuery("latitude").gte(latitude - LATITUDE_1KM).lte(latitude + LATITUDE_1KM));
        query.filter(QueryBuilders.rangeQuery("longitude").gte(longitude - LONGITUDE_1KM).lte(longitude + LONGITUDE_1KM));

        SearchRequest searchRequest = new SearchRequest(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion());
        searchRequest.source(new SearchSourceBuilder().query(query));

        SearchResponse response;
        try (RestHighLevelClient searchClient = searchClient()) {
            response = searchClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @Override
    public SearchResponse getKeywordSuggestions(OpenSearchIndex openSearchIndex, String keyword) {
        MatchPhrasePrefixQueryBuilder matchPhrasePrefixQueryBuilder = QueryBuilders.matchPhrasePrefixQuery(openSearchIndex.getFieldName(), keyword);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(12)
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .query(matchPhrasePrefixQueryBuilder);

        SearchRequest searchRequest = new SearchRequest()
                .indices(openSearchIndex.getIndexNameWithVersion())
                .source(searchSourceBuilder);

        SearchResponse response;
        try (RestHighLevelClient searchClient = searchClient()) {
            response = searchClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private RestHighLevelClient searchClient() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(openSearchId, openSearchPassword));

        RestClientBuilder builder = RestClient.builder(new HttpHost(openSearchHost, 443, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);

    }

    @Override
    public void loadEntireData() throws IOException {
        this.convertLoadData(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.bakeryLoadEntireDataJPQLQuery());
        log.info("========================= loadEntireData " + OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion() + " has been finished =========================");

        this.convertLoadData(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.breadLoadEntireDataJPQLQuery());
        log.info("========================= loadEntireData " + OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion() + " has been finished =========================");
    }

    @Override
    public void loadHourlyData() throws IOException {
        this.convertLoadData(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.bakeryLoadHourlyDataJPQLQuery());
        log.info("========================= loadHourlyData " + OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion() + " has been finished =========================");

        this.convertLoadData(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.breadLoadHourlyDataJPQLQuery());
        log.info("========================= loadHourlyData " + OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion() + " has been finished =========================");
    }

    private void convertLoadData(String indexName, List<? extends CommonLoadData> loadList) throws IOException {
        for (CommonLoadData loadItem : loadList) {

            String bakeryName = loadItem.getBakeryName();

            HashMap<String, String> loadHashMap = new HashMap<>();
            loadHashMap.put("bakeryId", String.valueOf(loadItem.getBakeryId()));
            loadHashMap.put("bakeryName", bakeryName);
            loadHashMap.put("bakeryAddress", loadItem.getBakeryAddress());
            loadHashMap.put("longitude", String.valueOf(loadItem.getLongitude()));
            loadHashMap.put("latitude", String.valueOf(loadItem.getLatitude()));
            loadHashMap.put("totalScore", String.valueOf(loadItem.getTotalScore()));
            loadHashMap.put("reviewCount", String.valueOf(loadItem.getReviewCount()));
            loadHashMap.put("chosung", UnicodeHandler.splitHangulToChosung(bakeryName).stream().map(Object::toString).collect(Collectors.joining()));

            if (loadItem instanceof BreadLoadData bread) {
                String breadName = bread.getBreadName();
                loadHashMap.put("breadId", String.valueOf(bread.getBreadId()));

                String parsedBreadName = parseEndingWithNumberAndSizeInKorean(breadName);
                loadHashMap.put("breadName", parsedBreadName);
                loadHashMap.put("chosung", UnicodeHandler.splitHangulToChosung(parsedBreadName).stream().map(Object::toString).collect(Collectors.joining()));
            }

            this.addDataToIndex(indexName, loadHashMap);
        }
    }

    // Ref: https://www.notion.so/e605c39a5af64ed69cfba9c9259937cc
    public static String parseEndingWithNumberAndSizeInKorean(String input) {
        String collapsedSpacesResult = input.toString().replaceAll("\\s+", "");

        String regex = ".*?([0-9]+[가-힣]|세트)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(collapsedSpacesResult);

        if (matcher.find()) {
            String parsedString = matcher.group(1);

            Pattern specialCharsPattern = Pattern.compile("[^a-zA-Z0-9]+");
            Matcher specialCharsMatcher = specialCharsPattern.matcher(parsedString);

            StringBuilder result = new StringBuilder();
            while (specialCharsMatcher.find()) {
                result.append(specialCharsMatcher.group());
            }
            return result.toString();
        } else {
            return input;
        }
    }

}
