package com.depromeet.breadmapbackend.domain.admin.feed.controller;

import static com.fasterxml.jackson.databind.node.JsonNodeType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.dto.FeedLikeResponse;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationBakery;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CommonFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CurationFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.LandingFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

public class FeedControllerTest extends ControllerTest {

	private FeedRequestDto curationRequest;
	private FeedRequestDto landingRequest;
	private Category category;
	private JwtToken token;
	private Admin admin;
	private User user;
	private CurationFeed curation;
	private LandingFeed landing;

	@BeforeEach
	public void setup() throws IOException {
		admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
		adminRepository.save(admin);
		token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

		user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);

		Category cate = Category.builder().categoryName("test category").build();
		category = categoryRepository.save(cate);

		Bakery firstBakery = Bakery.builder()
			.address("address")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.facilityInfoList(facilityInfo)
			.name("test bakery 1")
			.status(BakeryStatus.POSTING)
			.image(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg")
			.build();

		Bakery secondBakery = Bakery.builder()
			.address("address")
			.latitude(37.5596080751252)
			.longitude(127.044235146211)
			.facilityInfoList(facilityInfo)
			.name("test bakery 2")
			.status(BakeryStatus.POSTING)
			.image(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg")
			.build();

		bakeryRepository.save(firstBakery);
		bakeryRepository.save(secondBakery);

		Flag testFlag = flagRepository.save(Flag.builder()
			.user(user)
			.name("test flag")
			.color(FlagColor.BLUE)
			.build());

		FlagBakery testFlagBakery = flagBakeryRepository.save(FlagBakery.builder()
			.bakery(firstBakery)
			.flag(testFlag)
			.user(user)
			.build());

		s3Uploader.upload(
			new MockMultipartFile("image", "bakeryImage.jpg", "image/jpg", "test".getBytes()),
			"bakeryImage.jpg");

		Product firstProduct = Product.builder()
			.bakery(firstBakery)
			.productType(ProductType.BREAD)
			.name("bread1")
			.price("30000")
			.image(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.build();

		Product secondProduct = Product.builder()
			.bakery(secondBakery)
			.productType(ProductType.BREAD)
			.name("bread2")
			.price("50000")
			.image(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.build();

		productRepository.save(firstProduct);
		productRepository.save(secondProduct);

		s3Uploader.upload(
			new MockMultipartFile("image", "tempImage.jpg", "image/jpg", "test".getBytes()),
			"tempImage.jpg");

		CommonFeedRequestDto commonCuration = CommonFeedRequestDto.builder()
			.subTitle("큐레이션 테스트 피드")
			.introduction("큐레이션 테스트 피드 서론")
			.conclusion("큐레이션 테스트 피드 결론")
			.categoryId(category.getId())
			.thumbnailUrl(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.activated(FeedStatus.POSTING)
			.feedType(FeedType.CURATION)
			.activeTime("2023-01-01T00:00:00")
			.build();

		CommonFeedRequestDto commonLanding = CommonFeedRequestDto.builder()
			.subTitle("랜딩 테스트 피드")
			.introduction("랜딩 테스트 피드 서론")
			.conclusion("랜딩 테스트 피드 결론")
			.categoryId(category.getId())
			.thumbnailUrl(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.activated(FeedStatus.POSTING)
			.feedType(FeedType.LANDING)
			.activeTime("2023-01-01T00:00:01")
			.build();

		List<CurationFeedRequestDto> curationDto = List.of(
			CurationFeedRequestDto.builder()
				.bakeryId(firstBakery.getId())
				.productId(firstProduct.getId())
				.reason("test reason for first bakery")
				.build(),
			CurationFeedRequestDto.builder()
				.bakeryId(secondBakery.getId())
				.productId(secondProduct.getId())
				.reason("test reason for second bakery")
				.build()
		);

		LandingFeedRequestDto landingDto = new LandingFeedRequestDto("/testRedirectUrl");

		curationRequest = new FeedRequestDto(commonCuration, curationDto);
		landingRequest = new FeedRequestDto(commonLanding, landingDto);

		landing = (LandingFeed)feedRepository.save(FeedAssembler.toEntity(admin, category, landingRequest));

		List<Bakery> bakeries = List.of(firstBakery, secondBakery);

		CurationFeed curationEntity = (CurationFeed)FeedAssembler.toEntity(admin, category, curationRequest);

		List<CurationBakery> curationBakeries = FeedAssembler.toCurationBakery(curationEntity, bakeries,
			curationRequest);

		curationEntity.addAll(bakeries, curationBakeries);

		curation = feedRepository.saveAndFlush(curationEntity);

		commonFeedService.likeFeed(user.getId(), curation.getId());
		commonFeedService.likeFeed(user.getId(), curation.getId());
		commonFeedService.likeFeed(user.getId(), curation.getId());

		curationFeedRepository.flush();
		feedRepository.flush();
	}

	@AfterEach
	public void setDown() {
		// flagBAkery, feedLikeRepository
		bakeryUpdateReportImageRepository.deleteAllInBatch();
		bakeryUpdateReportRepository.deleteAllInBatch();
		bakeryReportImageRepository.deleteAllInBatch();
		productAddReportImageRepository.deleteAllInBatch();
		productAddReportRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		bakeryViewRepository.deleteAllInBatch();
		curationBakeryRepository.deleteAllInBatch();
		bakeryRepository.deleteAllInBatch();
		curationFeedRepository.deleteAllInBatch();
		landingFeedRepository.deleteAllInBatch();
		feedRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		adminRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("홈 화면의 모든 피드를 조회한다 - 유저")
	void 홈_화면의_모든_피드_조회() throws Exception {

		//given
		List<Feed> feedList = List.of(landing, curation);

		List<FeedResponseForUser> content = FeedAssembler.toDtoForUser(feedList);

		ApiResponse<List<FeedResponseForUser>> res = new ApiResponse<>(content);

		String response = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/feed/all")
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE));

		perform.andExpect(status().isOk())
			.andExpect(content().string(response));

		//then
		perform.andDo(print()).
			andDo(document("findall-feed-user",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					responseFields(
						fieldWithPath("data.[].feedId").type(NUMBER).description("피드 아이디"),
						fieldWithPath("data.[].imageUrl").type(STRING).description("피드 이미지"),
						fieldWithPath("data.[].feedType").type(STRING).description("피드 타입"),
						fieldWithPath("data.[].redirectUrl").type(STRING).optional().description("RedirectUrl (랜딩 타입만 사용)")
					)
				)
			);
	}

	// @Test
	// @DisplayName("랜딩 피드 상세 조회 - 유저")
	// void 랜딩_피드_상세_조회_유저() throws Exception {
	//
	// 	//given
	// 	FeedResponseDto response = FeedResponseDto.builder()
	// 		.common(FeedAssembler.toCommonDto(landing))
	// 		.landing(FeedAssembler.toLandingDto(landing))
	// 		.build();
	// 	ApiResponse<FeedResponseDto> res = new ApiResponse<>(response);
	// 	String content = objectMapper.writeValueAsString(res);
	//
	// 	//when
	// 	ResultActions perform = mockMvc.perform(get("/v1/feed/{feedId}", landing.getId())
	// 		.header("Authorization", "Bearer " + token.getAccessToken())
	// 		.accept(MediaType.APPLICATION_JSON_VALUE)
	// 		.contentType(MediaType.APPLICATION_JSON_VALUE)
	// 		.param("feedType", "landing"));
	//
	// 	perform.andExpect(status().isOk())
	// 		.andExpect(content().string(content));
	//
	// 	//then
	// 	perform.andDo(print()).
	// 		andDo(document("find-detail-landing-feed-user",
	// 				preprocessRequest(prettyPrint()),
	// 				preprocessResponse(prettyPrint()),
	// 				requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
	// 				pathParameters(
	// 					parameterWithName("feedId").description("피드 아이디")
	// 				),
	// 				requestParameters(
	// 					parameterWithName("feedType").description("피드 타입(LANDING, CURATION)")
	// 				),
	// 				responseFields(
	// 					fieldWithPath("data.common.feedId").description("피드 소제목"),
	// 					fieldWithPath("data.common.subTitle").description("피드 소제목"),
	// 					fieldWithPath("data.common.introduction").description("피드 시작하는 말"),
	// 					fieldWithPath("data.common.conclusion").description("피드 끝맺음 말"),
	// 					fieldWithPath("data.common.thumbnailUrl").description("피드 배너 이미지 url"),
	// 					fieldWithPath("data.common.categoryName").description("카테고리 이름"),
	// 					fieldWithPath("data.common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
	// 					fieldWithPath("data.common.feedType").description("피드 타입(LANDING, CURATION)"),
	// 					fieldWithPath("data.common.activateTime").description("피드 게시 시작 날짜"),
	// 					fieldWithPath("data.curation").optional().description("null"),
	// 					fieldWithPath("data.landing.redirectUrl").description("redirectURl"),
	// 					fieldWithPath("data.likeCounts").description("현재 피드 좋아요 개수"),
	// 					fieldWithPath("data.likeStatus").description("현재 조회하고 있는 유저의 피드 좋아요 상태"))
	// 			)
	// 		);
	// }

	@Test
	@DisplayName("큐레이션 피드 상세 조회 - 유저")
	void 큐레이션_피드_상세_조회_유저() throws Exception {

		//given
		List<Product> products = productRepository.findByIdIn(curation.getBakeries().getProductIdList());
		List<Bakery> bakeries = products.stream().map(Product::getBakery).collect(Collectors.toList());
		List<FlagBakery> isFlagged = flagBakeryRepository.findByUserIdAndBakeryIdIn(user.getId(),
			bakeries.stream().map(Bakery::getId).collect(Collectors.toList()));

		FeedResponseDto response = FeedResponseDto.builder()
			.common(FeedAssembler.toCommonDto(curation))
			.curation(FeedAssembler.toCurationDto(bakeries, products))
			.likeCounts(3)
			.likeStatus("LIKE")
			.build();

		response.setIsFlagged(isFlagged);
		response.setRecommendReason(curation.getBakeries().getCurationBakeries());

		ApiResponse<FeedResponseDto> res = new ApiResponse<>(response);
		String content = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/feed/{feedId}", curation.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.param("feedType", "curation"));

		perform.andExpect(status().isOk())
			.andExpect(content().string(content));

		//then
		perform.andDo(print()).
			andDo(document("find-detail-curation-feed-user",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					pathParameters(
						parameterWithName("feedId").description("피드 아이디")
					),
					requestParameters(
						parameterWithName("feedType").description("피드 타입(LANDING, CURATION)")
					),
					responseFields(
						fieldWithPath("data.common.feedId").type(NUMBER).description("피드 아이디"),
						fieldWithPath("data.common.subTitle").type(STRING).description("피드 소제목"),
						fieldWithPath("data.common.introduction").type(STRING).description("피드 시작하는 말"),
						fieldWithPath("data.common.conclusion").type(STRING).description("피드 끝맺음 말"),
						fieldWithPath("data.common.thumbnailUrl").type(STRING).description("피드 배너 이미지 url"),
						fieldWithPath("data.common.categoryName").type(STRING).description("카테고리 이름"),
						fieldWithPath("data.common.activated").type(STRING).description("피드 활성화 여부(POSTING, INACTIVATED)"),
						fieldWithPath("data.common.feedType").type(STRING).description("피드 타입(LANDING, CURATION)"),
						fieldWithPath("data.common.activateTime").type(STRING).description("피드 게시 시작 날짜"),
						fieldWithPath("data.curation.[].bakeryId").type(NUMBER).description("큐레이션 피드 빵집 ID"),
						fieldWithPath("data.curation.[].bakeryName").type(STRING).description("큐레이션 피드 빵집 이름"),
						fieldWithPath("data.curation.[].bakeryAddress").type(STRING).description("큐레이션 피드 빵집 주소"),
						fieldWithPath("data.curation.[].openingHours").type(STRING).description("큐레이션 피드 빵집 오픈시각"),
						fieldWithPath("data.curation.[].bakeryImageUrl").type(STRING).description("큐레이션 피드 빵집 이미지 Url"),
						fieldWithPath("data.curation.[].checkPoint").type(STRING).description("큐레이션 피드 빵집 체크포인트"),
						fieldWithPath("data.curation.[].newBreadTime").type(STRING).description("큐레이션 피드 빵집 갓군빵 나오는 시간"),
						fieldWithPath("data.curation.[].address").type(STRING).description("큐레이션 피드 추천 빵집 도로명 주소"),
						fieldWithPath("data.curation.[].detailedAddress").type(STRING)
							.description("큐레이션 피드 추천 빵집 도로명 상세 주소"),
						fieldWithPath("data.curation.[].websiteURL").type(STRING).description("큐레이션 피드 추천 빵집 website URL"),
						fieldWithPath("data.curation.[].instagramURL").type(STRING)
							.description("큐레이션 피드 추천 빵집 Instagram URL"),
						fieldWithPath("data.curation.[].facebookURL").type(STRING)
							.description("큐레이션 피드 추천 빵집 FaceBook URL"),
						fieldWithPath("data.curation.[].blogURL").type(STRING).description("큐레이션 피드 추천 빵집 블로그 URL"),
						fieldWithPath("data.curation.[].facilityInfo").type(STRING).description("큐레이션 피드 추천 빵집 부대시설"),
						fieldWithPath("data.curation.[].phoneNumber").type(STRING).description("큐레이션 피드 추천 빵집 전화번호"),
						fieldWithPath("data.curation.[].reason").type(STRING).description("큐레이션 피드 빵집 추천 이유"),
						fieldWithPath("data.curation.[].flagged").type(BOOLEAN).description("큐레이션 피드 빵집 플래그 저장 여부"),
						fieldWithPath("data.curation.[].productId").type(NUMBER).description("큐레이션 피드 빵집 상품 ID"),
						fieldWithPath("data.curation.[].productName").type(STRING).description("큐레이션 피드 빵집 상품 이름"),
						fieldWithPath("data.curation.[].productPrice").type(STRING).description("큐레이션 피드 빵집 상품 가격"),
						fieldWithPath("data.curation.[].productImageUrl").type(STRING).description("큐레이션 피드 빵집 상품 이미지 Url"),
						fieldWithPath("data.landing").optional().description("null"),
						fieldWithPath("data.likeCounts").type(NUMBER).description("현재 피드 좋아요 개수"),
						fieldWithPath("data.likeStatus").type(STRING).description("현재 유저가 피드 좋아요 한 상태"))
				)
			);
	}

	@Test
	@DisplayName("피드 좋아요 테스트")
	void 유저는_피드_좋아요를_할수있다() throws Exception {

		//given
		FeedLikeResponse response = commonFeedService.likeFeed(user.getId(), curation.getId());

		FeedLikeResponse expected = FeedLikeResponse.builder()
			.userId(user.getId())
			.likeStatus("LIKE")
			.likeCounts(response.getLikeCounts() + 1)
			.build();

		ApiResponse<FeedLikeResponse> expectedResponse = new ApiResponse<>(expected);
		String expectedToString = objectMapper.writeValueAsString(expectedResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/v1/feed/{feedId}/like", curation.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE));

		perform.andExpect(status().isOk())
			.andExpect(content().string(expectedToString));

		//then
		perform.andDo(print()).
			andDo(document("like-curation-feed-user",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					pathParameters(
						parameterWithName("feedId").description("피드 아이디")
					),
					responseFields(
						fieldWithPath("data.userId").type(NUMBER).description("유저 아이디"),
						fieldWithPath("data.likeStatus").type(STRING).description("유저 좋아요 상태(좋아요 체크)"),
						fieldWithPath("data.likeCounts").type(NUMBER).description("현재 피드에 좋아요 찍은 횟수"))
				)
			);
	}
}
