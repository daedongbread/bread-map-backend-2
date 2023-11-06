package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.keyword.request.OpenSearchAddDataRequest;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.Test;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchResponse;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OpenSearchControllerTest extends ControllerTest {

    @MockBean
    private OpenSearchService openSearchService;

    @Test
    public void testAddDataToIndex() throws Exception {

        OpenSearchAddDataRequest request = new OpenSearchAddDataRequest();
        when(openSearchService.addDataToIndex(request.getIndexName(), request.getStringMapping()))
                .thenReturn(mock(IndexResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/search-engine/document")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andDo(document("v1/search-engine/document",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("indexName").description("OpenSearch 인덱스명"),
                                fieldWithPath("stringMapping").description("OpenSearch 검색 쿼리 input params")
                        ),
                        responseFields(
                                fieldWithPath("data.shardInfo").description("shardInfo"),
                                fieldWithPath("data.shardId").description("shardId"),
                                fieldWithPath("data.id").description("id"),
                                fieldWithPath("data.version").description("version"),
                                fieldWithPath("data.seqNo").description("seqNo"),
                                fieldWithPath("data.primaryTerm").description("primaryTerm"),
                                fieldWithPath("data.result").description("result"),
                                fieldWithPath("data.index").description("index"),
                                fieldWithPath("data.fragment").description("fragment")
                        )
                ))
                .andExpect(status().isOk());

        verify(openSearchService, times(1))
                .addDataToIndex(request.getIndexName(), request.getStringMapping());
    }

    @Test
    public void testGetBreadByKeyword() throws Exception {
        String keyword = "베이커리";
        when(openSearchService.getBreadByKeyword(keyword))
                .thenReturn(mock(SearchResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/search-engine/document/bread")
                        .param("keyword", keyword))
                .andDo(print())
                .andDo(document("v1/search-engine/document/bread",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("keyword").description("OpenSearch 검색 키워드")
                        ),
                        responseFields(
                                fieldWithPath("data.internalResponse").description("internalResponse"),
                                fieldWithPath("data.scrollId").description("scrollId"),
                                fieldWithPath("data.totalShards").description("totalShards"),
                                fieldWithPath("data.successfulShards").description("successfulShards"),
                                fieldWithPath("data.skippedShards").description("skippedShards"),
                                fieldWithPath("data.shardFailures").description("shardFailures"),
                                fieldWithPath("data.clusters").description("clusters"),
                                fieldWithPath("data.terminatedEarly").description("terminatedEarly"),
                                fieldWithPath("data.failedShards").description("failedShards"),
                                fieldWithPath("data.numReducePhases").description("numReducePhases"),
                                fieldWithPath("data.aggregations").description("aggregations"),
                                fieldWithPath("data.profileResults").description("profileResults"),
                                fieldWithPath("data.hits").description("hits"),
                                fieldWithPath("data.timedOut").description("timedOut"),
                                fieldWithPath("data.suggest").description("suggest"),
                                fieldWithPath("data.took").description("took"),
                                fieldWithPath("data.fragment").description("fragment")
                        )
                ))

                .andExpect(status().isOk());

        verify(openSearchService, times(1))
                .getBreadByKeyword(keyword);
    }

    @Test
    public void testGetBakeryByKeyword() throws Exception {
        String keyword = "베이커리";

        when(openSearchService.getBakeryByKeyword(keyword))
                .thenReturn(mock(SearchResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/search-engine/document/bakery")
                        .param("keyword", keyword))
                .andDo(print())
                .andDo(document("v1/search-engine/document/bakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("keyword").description("OpenSearch 검색 키워드")
                        ),
                        responseFields(
                                fieldWithPath("data.internalResponse").description("internalResponse"),
                                fieldWithPath("data.scrollId").description("scrollId"),
                                fieldWithPath("data.totalShards").description("totalShards"),
                                fieldWithPath("data.successfulShards").description("successfulShards"),
                                fieldWithPath("data.skippedShards").description("skippedShards"),
                                fieldWithPath("data.shardFailures").description("shardFailures"),
                                fieldWithPath("data.clusters").description("clusters"),
                                fieldWithPath("data.terminatedEarly").description("terminatedEarly"),
                                fieldWithPath("data.failedShards").description("failedShards"),
                                fieldWithPath("data.numReducePhases").description("numReducePhases"),
                                fieldWithPath("data.aggregations").description("aggregations"),
                                fieldWithPath("data.profileResults").description("profileResults"),
                                fieldWithPath("data.hits").description("hits"),
                                fieldWithPath("data.timedOut").description("timedOut"),
                                fieldWithPath("data.suggest").description("suggest"),
                                fieldWithPath("data.took").description("took"),
                                fieldWithPath("data.fragment").description("fragment")
                        )
                ))
                .andExpect(status().isOk());

        verify(openSearchService, times(1)).getBakeryByKeyword(keyword);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
