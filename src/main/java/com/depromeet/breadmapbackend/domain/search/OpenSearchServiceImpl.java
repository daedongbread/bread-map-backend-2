package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.admin.openSearch.dto.response.OpenSearchCreateIndexResponse;
import com.depromeet.breadmapbackend.domain.bakery.BakeryQueryRepository;
import com.depromeet.breadmapbackend.domain.search.dto.OpenSearchIndex;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.CommonLoadData;
import com.depromeet.breadmapbackend.domain.search.utils.HanguelJamoMorphTokenizer;
import com.depromeet.breadmapbackend.domain.search.utils.UnicodeHandleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.*;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final static Double LATITUDE_1KM = 1 / 109.958489;
    private final static Double LONGITUDE_1KM = 1 / 88.74;

    private final BakeryQueryRepository bakeryQueryRepository;

    @Override
    public OpenSearchCreateIndexResponse deleteAndCreateIndex(String indexName) throws IOException {
        try (RestHighLevelClient searchClient = searchClient()) {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            searchClient.indices().delete(request, RequestOptions.DEFAULT);

            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

            createIndexRequest.settings(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("number_of_shards", 1)
                            .field("number_of_replicas", 0)
                            .field("max_ngram_diff", 30)
                            .startObject("analysis")
                            .startObject("filter")
                            .startObject("daedong-synonym")
                            .field("type", "synonym")
                            .field("synonyms_path", "analyzers/F217539104")
                            .field("updatable", true)
                            .endObject()
                            .endObject()
                            .startObject("analyzer")
                            .startObject("analyzer-daedong")
                            .field("type", "custom")
                            .field("tokenizer", "seunjeon_tokenizer")
                            .endObject()
                            .startObject("synonym-daedong")
                            .field("type", "custom")
                            .field("tokenizer", "standard")
                            .field("filter", "daedong-synonym")
                            .endObject()
                            .startObject("ngram-daedong")
                            .field("type", "custom")
                            .field("tokenizer", "partial")
                            .field("filter", "lowercase")
                            .endObject()
                            .startObject("edge-front-daedong")
                            .field("type", "custom")
                            .field("tokenizer", "edgefront")
                            .field("filter", "lowercase")
                            .endObject()
                            .startObject("edge-back-daedong")
                            .field("type", "custom")
                            .field("tokenizer", "edgeback")
                            .field("filter", "lowercase")
                            .endObject()
                            .endObject()
                            .startObject("tokenizer")
                            .startObject("partial")
                            .field("type", "ngram")
                            .field("min_gram", "1")
                            .field("max_gram", "30")
                            .array("token_chars", new String[]{"letter", "digit"})
                            .endObject()
                            .startObject("edgefront")
                            .field("type", "edge_ngram")
                            .field("min_gram", "1")
                            .field("max_gram", "30")
                            .array("token_chars", new String[]{"letter", "digit"})
                            .endObject()
                            .startObject("edgeback")
                            .field("type", "edge_ngram")
                            .field("min_gram", "1")
                            .field("max_gram", "30")
                            .array("token_chars", new String[]{"letter", "digit"})
                            .endObject()
                            .endObject()
                            .startObject("normalizer")
                            .startObject("normalizer-daedong")
                            .field("type", "custom")
                            .field("filter", "lowercase")
                            .endObject()
                            .endObject()
                            .endObject()
                            .endObject()

            );

            String source = """
                    {
                        "_source": {
                          "excludes": [
                            "chosung",
                            "jamo",
                            "engtokor"
                          ]
                        },
                        "properties": {
                          "id": {
                            "type": "keyword",
                            "index": false
                          },
                          "description": {
                            "type": "text",
                            "analyzer": "standard",
                            "search_analyzer": "synonym-daedong"
                          },
                          "indexName": {
                            "type": "text",
                            "analyzer": "analyzer-daedong",
                            "fields" : {
                              "exact": {
                                "type": "keyword",
                                "normalizer": "normalizer-daedong"
                              },
                              "front": {
                                "type": "text",
                                "analyzer": "edge-front-daedong"
                              },
                              "back": {
                                "type": "text",
                                "analyzer": "edge-back-daedong"
                              },
                              "partial": {
                                "type": "text",
                                "analyzer": "ngram-daedong"
                              }
                            }
                          },
                          "chosung": {
                            "type": "text",
                            "analyzer": "edge-front-daedong",
                            "fields" : {
                              "exact": {
                                "type": "keyword",
                                "normalizer": "normalizer-daedong"
                              },
                              "back": {
                                "type": "text",
                                "analyzer": "edge-back-daedong"
                              },
                              "partial": {
                                "type": "text",
                                "analyzer": "ngram-daedong"
                              }
                            }
                          },
                          "jamo": {
                            "type": "text",
                            "analyzer": "edge-front-daedong",
                            "fields" : {
                              "exact": {
                                "type": "keyword",
                                "normalizer": "normalizer-daedong"
                              },
                              "back": {
                                "type": "text",
                                "analyzer": "edge-back-daedong"
                              },
                              "partial": {
                                "type": "text",
                                "analyzer": "ngram-daedong"
                              }
                            }
                          },
                          "engtokor": {
                            "type": "text",
                            "analyzer": "edge-front-daedong",
                            "fields" : {
                              "exact": {
                                "type": "keyword",
                                "normalizer": "normalizer-daedong"
                              },
                              "back": {
                                "type": "text",
                                "analyzer": "edge-back-daedong"
                              },
                              "partial": {
                                "type": "text",
                                "analyzer": "ngram-daedong"
                              }
                            }
                          }
                        }
                      }""";

            String renamedSource = source.replace("indexName", indexName);
            createIndexRequest.mapping(renamedSource, XContentType.JSON);
            searchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (OpenSearchStatusException osse) {
            return new OpenSearchCreateIndexResponse(osse.getMessage());

        }
        return new OpenSearchCreateIndexResponse(indexName + " :: 인덱스 생성에 성공했습니다.");

    }

    @Override
    public void deleteIndex(OpenSearchIndex openSearchIndex, Long targetId) throws IOException {
        //Adding data to the index.
        try (RestHighLevelClient searchClient = searchClient()) {
            DeleteByQueryRequest request = new DeleteByQueryRequest(openSearchIndex.name());
            if(OpenSearchIndex.BAKERY_SEARCH == openSearchIndex) {
                request.setQuery(QueryBuilders.termQuery("bakeryId", "bakeryId"+targetId));

            } else if(OpenSearchIndex.BREAD_SEARCH == openSearchIndex) {
                request.setQuery(QueryBuilders.termQuery("breadId", "breadId"+targetId));
            }

            searchClient.deleteByQuery(request, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void deleteAllBreads(Long bakeryId) throws IOException {
        try (RestHighLevelClient searchClient = searchClient()) {
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion());
            deleteByQueryRequest.setQuery(QueryBuilders.matchQuery("bakeryId", bakeryId));

            searchClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public IndexResponse addDataToIndex(String indexName, HashMap<String, String> stringMapping) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        IndexResponse response = null;
        try (RestHighLevelClient searchClient = searchClient()) {
            IndexRequest request = new IndexRequest(indexName);

            if (indexName.equals(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion())) {
                request.id("bakery" + stringMapping.get("bakeryId"));
            } else if (indexName.equals(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion())) {
                request.id("bread" + stringMapping.get("breadId"));
            }

            request.source(stringMapping);

            Request requestDoc = new Request("POST", "/bakery-search-x1/_doc/");
            requestDoc.setJsonEntity(objectMapper.writeValueAsString(stringMapping));
            try {
                response = searchClient.index(request, RequestOptions.DEFAULT);
                searchClient.getLowLevelClient().performRequest(requestDoc);

            } catch (ConnectException ce) {
                log.debug("addDataToIndex :: " + ce.getMessage());
            }
            return response;
        }
    }

    @Override
    public SearchResponse getBakeryByKeyword(String keyword) {

//        String source = """
//                {
//                  "query": {
//                    "bool": {
//                      "should": [
//                        {
//                          "match": {
//                            "bakeryName": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "bakeryName.keyword": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "bakeryAddress": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "bakeryAddress.keyword": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.exact": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.exact": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.exact": "inputKeyword"
//                          }
//                        }
//                      ]
//                    }
//                  }
//                }
//                """;

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.matchQuery("bakeryName", keyword));
        boolQuery.should(QueryBuilders.matchQuery("bakeryName.keyword", keyword));
        boolQuery.should(QueryBuilders.matchQuery("bakeryAddress", keyword));
        boolQuery.should(QueryBuilders.matchQuery("bakeryAddress.keyword", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("description", keyword));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(7)
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .query(boolQuery);

        SearchRequest searchRequest = new SearchRequest()
                .indices(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion())
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
    public SearchResponse getBreadByKeyword(String keyword) {

//        String source = """
//                {
//                    "bool": {
//                      "should": [
//                        {
//                          "match": {
//                            "breadName": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "breadName.keyword": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "jamo.exact": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "engtokor.exact": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.back": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.partial": "inputKeyword"
//                          }
//                        },
//                        {
//                          "match": {
//                            "chosung.exact": "inputKeyword"
//                          }
//                        }
//                      ]
//                    }
//                }
//                """;

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Create match queries for different fields
        boolQuery.should(QueryBuilders.matchQuery("breadName", keyword));
        boolQuery.should(QueryBuilders.matchQuery("breadName.keyword", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("description", keyword));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(7)
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .query(boolQuery);

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

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Create match queries for different fields
        boolQuery.should(QueryBuilders.matchQuery("breadName", keyword));
        boolQuery.should(QueryBuilders.matchQuery("breadName.keyword", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("jamo.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("engtokor.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.back", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.partial", keyword));
        boolQuery.should(QueryBuilders.matchQuery("chosung.exact", keyword));
        boolQuery.should(QueryBuilders.matchQuery("description", keyword));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(12)
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                .query(boolQuery);

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
        this.convertDataAndLoadToEngine(OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.bakeryLoadEntireDataJPQLQuery());
        log.info("========================= loadEntireData " + OpenSearchIndex.BAKERY_SEARCH.getIndexNameWithVersion() + " has been finished =========================");

        this.convertDataAndLoadToEngine(OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion(), bakeryQueryRepository.breadLoadEntireDataJPQLQuery());
        log.info("========================= loadEntireData " + OpenSearchIndex.BREAD_SEARCH.getIndexNameWithVersion() + " has been finished =========================");
    }

    @Override
    public void convertDataAndLoadToEngine(String indexName, List<? extends CommonLoadData> loadList) throws IOException {

        final BulkRequest bulkRequest = new BulkRequest();
        final HanguelJamoMorphTokenizer tokenizer = HanguelJamoMorphTokenizer.getInstance();

        for (CommonLoadData loadItem : loadList) {

            String bakeryName = loadItem.getBakeryName();

            Map<String, Object> loadHashMap = new HashMap<>();
            loadHashMap.put("bakeryId", loadItem.getBakeryId());
            loadHashMap.put("bakeryName", bakeryName);
            loadHashMap.put("description", bakeryName);
            loadHashMap.put("bakeryAddress", loadItem.getBakeryAddress());
            loadHashMap.put("longitude", String.valueOf(loadItem.getLongitude()));
            loadHashMap.put("latitude", String.valueOf(loadItem.getLatitude()));

            if(loadItem instanceof BakeryLoadData) {
                loadHashMap.put("chosung", tokenizer.chosungTokenizer(bakeryName));
                loadHashMap.put("jamo", UnicodeHandleUtils.splitHangulToConsonant(bakeryName));
                loadHashMap.put("engtokor", tokenizer.convertKoreanToEnglish(bakeryName));
            }

            if (loadItem instanceof BreadLoadData bread) {
                String breadName = bread.getBreadName();
                loadHashMap.put("breadId", bread.getBreadId());
                loadHashMap.put("description", breadName);

                String parsedBreadName = parseEndingWithNumberAndSizeInKorean(breadName);
                loadHashMap.put("breadName", parsedBreadName);
                loadHashMap.put("chosung", tokenizer.chosungTokenizer(breadName));
                loadHashMap.put("jamo", UnicodeHandleUtils.splitHangulToConsonant(breadName));
                loadHashMap.put("engtokor", tokenizer.convertKoreanToEnglish(breadName));
            }

            bulkRequest.add(new IndexRequest(indexName)
                    .id(String.valueOf(String.valueOf(loadItem.getBakeryId())))
                    .source(loadHashMap));
        }

        try (RestHighLevelClient searchClient = searchClient()) {
            searchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
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
