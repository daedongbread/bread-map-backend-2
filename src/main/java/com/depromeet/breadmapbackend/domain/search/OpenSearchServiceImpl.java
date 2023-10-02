package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.bakery.BakeryQueryRepository;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.CommonLoadData;
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
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchServiceImpl implements OpenSearchService {

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
            } catch(OpenSearchException ose) {
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
            IndexRequest request = new IndexRequest(indexName); //Add a document to the custom-index we created.
            request.source(stringMapping); //Place your content into the index's source.
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

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(7);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("breadName", keyword));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());
        searchRequest.source(searchSourceBuilder);

        SearchResponse response;
        try (RestHighLevelClient searchClient = searchClient()) {
            // perform the search
            response = searchClient.search(searchRequest, RequestOptions.DEFAULT);

            if(response.getHits().getHits().length < 1) {
                searchSourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("bakeryName", keyword));
                searchRequest.source(searchSourceBuilder);
                response = searchClient.search(searchRequest, RequestOptions.DEFAULT);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private RestHighLevelClient searchClient() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("admin", "eoehdQkdrjator1@"));

        //Create a client.
        RestClientBuilder builder = RestClient.builder(new HttpHost("search-search-opensearch-ex4u7p7xj5m4qtqh7vy5dpjkha.ap-northeast-2.es.amazonaws.com", 443, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);
    }

    @Override
    public void loadData() throws IOException {
        this.loadData(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.bakeryLoadDailyDataJPQLQuery());
        log.info("========================= " + OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion() + " =========================");

        this.loadData(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.breadLoadDailyDataJPQLQuery());
        log.info("========================= " + OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion() + " =========================");

    }

    private void loadData(String indexName, List<? extends CommonLoadData> loadList) throws IOException {
        for (CommonLoadData loadItem : loadList) {
            HashMap<String, String> loadHashMap = new HashMap<>();
            loadHashMap.put("bakeryId", String.valueOf(loadItem.getBakeryId()));
            loadHashMap.put("bakeryName", loadItem.getBakeryName());
            loadHashMap.put("bakeryAddress", loadItem.getBakeryAddress());
            loadHashMap.put("longitude", String.valueOf(loadItem.getLongitude()));
            loadHashMap.put("latitude", String.valueOf(loadItem.getLatitude()));
            loadHashMap.put("totalScore", String.valueOf(loadItem.getTotalScore()));
            loadHashMap.put("reviewCount", String.valueOf(loadItem.getReviewCount()));

            if (loadItem instanceof BreadLoadData bread) {
                loadHashMap.put("breadId", String.valueOf(bread.getBakeryId()));
                loadHashMap.put("breadName", bread.getBreadName());
            }

            this.addDataToIndex(indexName, loadHashMap);
        }
    }

}

//    @Override
//    public GetResponse getDocument(String indexName, String id) {
//        GetResponse response;
//        try (RestHighLevelClient searchClient = searchClient()) {
//            GetRequest getRequest;
//            if(id == null) {
//                getRequest = new GetRequest(indexName);
//            } else {
//                getRequest = new GetRequest(indexName, id);
//            }
//            response = searchClient.get(getRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return response;
//    }
