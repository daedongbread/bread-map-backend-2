package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductReportRequest;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLike;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BakeryProductControllerTest extends ControllerTest {
    private Bakery bakery;
    private JwtToken token;

    @BeforeEach
    public void setup() {
        User user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().address("address1").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);

        Product product1 = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();
        Product product2 = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread2").price("4000").build();
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @AfterEach
    public void setDown() {
        productAddReportImageRepository.deleteAllInBatch();
        productAddReportRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void getProductList() throws Exception {
        mockMvc.perform(get("/v1/bakeries/{bakeryId}/products", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.[].id").description("상품 고유번호"),
                                fieldWithPath("data.[].productType").description("상품 종류"),
                                fieldWithPath("data.[].name").description("상품 이름"),
                                fieldWithPath("data.[].rating").description("상품 평점"),
                                fieldWithPath("data.[].reviewNum").description("상품 리뷰 수"),
                                fieldWithPath("data.[].price").description("상품 가격"),
                                fieldWithPath("data.[].image").description("상품 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void productAddReport() throws Exception {
        String object = objectMapper.writeValueAsString(ProductReportRequest.builder()
                .name("newBread").price("4000").images(List.of("image1", "image2")).build());

        mockMvc.perform(post("/v1/bakeries/{bakeryId}/product-add-reports", bakery.getId())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/report/product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestFields(
                                fieldWithPath("name").description("제보 상품 이름"),
                                fieldWithPath("price").description("제보 상품 가격"),
                                fieldWithPath("images").optional().description("제보 상품 이미지들")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void searchSimpleProductList() throws Exception {
        mockMvc.perform(get("/v1/bakeries/{bakeryId}/products/search?name=bread", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/review/product/search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(parameterWithName("name").description("검색 키워드")),
                        responseFields(
                                fieldWithPath("data.[].id").description("상품 고유 번호"),
                                fieldWithPath("data.[].productType").description("상품 종류"),
                                fieldWithPath("data.[].name").description("상품 이름"),
                                fieldWithPath("data.[].price").description("상품 가격"),
                                fieldWithPath("data.[].image").description("상품 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }
}
