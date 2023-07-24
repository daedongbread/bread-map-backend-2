package com.depromeet.breadmapbackend.domain.admin.feed.controller;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
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
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

public class FeedControllerTest extends ControllerTest {

	private FeedRequestDto curationRequest;
	private FeedRequestDto landingRequest;
	private Category category;
	private JwtToken token;
	private Admin admin;
	private CurationFeed curation;
	private LandingFeed landing;

	@BeforeEach
	public void setup() throws IOException {
		admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
		adminRepository.save(admin);
		token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);

		Category cate = new Category("test category");
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
				.reason("test reason")
				.build(),
			CurationFeedRequestDto.builder()
				.bakeryId(secondBakery.getId())
				.productId(secondProduct.getId())
				.reason("test reason")
				.build()
		);

		LandingFeedRequestDto landingDto = new LandingFeedRequestDto("/testRedirectUrl");

		curationRequest = new FeedRequestDto(commonCuration, curationDto);
		landingRequest = new FeedRequestDto(commonLanding, landingDto);

		landing = (LandingFeed)feedRepository.save(FeedAssembler.toEntity(admin, category, landingRequest));

		List<Bakery> bakeries = List.of(firstBakery, secondBakery);

		CurationFeed curationEntity = (CurationFeed)FeedAssembler.toEntity(admin, category, curationRequest);

		curationEntity.addAll(bakeries, curationRequest);

		curation = feedRepository.save(curationEntity);
	}

	@AfterEach
	public void setDown() {
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

		String response = objectMapper.writeValueAsString(content);

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
						fieldWithPath("[].feedId").description("피드 아이디"),
						fieldWithPath("[].imageUrl").description("피드 이미지"),
						fieldWithPath("[].feedType").description("피드 타입"),
						fieldWithPath("[].redirectUrl").optional().description("RedirectUrl (랜딩 타입만 사용)")
					)
				)
			);
	}

	@Test
	@DisplayName("랜딩 피드 상세 조회 - 유저")
	void 랜딩_피드_상세_조회_유저() throws Exception {

		//given
		FeedResponseDto response = FeedAssembler.toDto(landing);
		String content = objectMapper.writeValueAsString(response);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/feed/{feedId}", landing.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.param("feedType", "landing"));

		perform.andExpect(status().isOk())
			.andExpect(content().string(content));

		//then
		perform.andDo(print()).
			andDo(document("find-detail-landing-feed-user",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					pathParameters(
						parameterWithName("feedId").description("피드 아이디")
					),
					requestParameters(
						parameterWithName("feedType").optional().description("피드 타입(LANDING, CURATION)")
					),
					responseFields(
						fieldWithPath("common.feedId").description("피드 소제목"),
						fieldWithPath("common.subTitle").description("피드 소제목"),
						fieldWithPath("common.introduction").description("피드 시작하는 말"),
						fieldWithPath("common.conclusion").description("피드 끝맺음 말"),
						fieldWithPath("common.thumbnailUrl").description("피드 배너 이미지 url"),
						fieldWithPath("common.categoryName").description("카테고리 이름"),
						fieldWithPath("common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
						fieldWithPath("common.feedType").description("피드 타입(LANDING, CURATION)"),
						fieldWithPath("common.activateTime").description("피드 게시 시작 날짜"),
						fieldWithPath("curation").optional().description("null"),
						fieldWithPath("landing.redirectUrl").description("redirectURl"))
				)
			);
	}

	@Test
	@DisplayName("큐레이션 피드 상세 조회 - 유저")
	void 큐레이션_피드_상세_조회_유저() throws Exception {

		//given
		FeedResponseDto response = FeedAssembler.toDto(curation);
		String content = objectMapper.writeValueAsString(response);

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
						parameterWithName("feedType").optional().description("피드 타입(LANDING, CURATION)")
					),
					responseFields(
						fieldWithPath("common.feedId").description("피드 소제목"),
						fieldWithPath("common.subTitle").description("피드 소제목"),
						fieldWithPath("common.introduction").description("피드 시작하는 말"),
						fieldWithPath("common.conclusion").description("피드 끝맺음 말"),
						fieldWithPath("common.thumbnailUrl").description("피드 배너 이미지 url"),
						fieldWithPath("common.categoryName").description("카테고리 이름"),
						fieldWithPath("common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
						fieldWithPath("common.feedType").description("피드 타입(LANDING, CURATION)"),
						fieldWithPath("common.activateTime").description("피드 게시 시작 날짜"),
						fieldWithPath("curation.[].bakeryId").description("큐레이션 피드 빵집 ID"),
						fieldWithPath("curation.[].bakeryName").description("큐레이션 피드 빵집 이름"),
						fieldWithPath("curation.[].bakeryAddress").description("큐레이션 피드 빵집 주소"),
						fieldWithPath("curation.[].openingHours").description("큐레이션 피드 빵집 오픈시각"),
						fieldWithPath("curation.[].bakeryImageUrl").description("큐레이션 피드 빵집 이미지 Url"),
						fieldWithPath("curation.[].checkPoint").description("큐레이션 피드 빵집 체크포인트"),
						fieldWithPath("curation.[].newBreadTime").description("큐레이션 피드 빵집 갓군빵 나오는 시간"),
						fieldWithPath("curation.[].productId").description("큐레이션 피드 빵집 상품 ID"),
						fieldWithPath("curation.[].productName").description("큐레이션 피드 빵집 상품 이름"),
						fieldWithPath("curation.[].productPrice").description("큐레이션 피드 빵집 상품 가격"),
						fieldWithPath("curation.[].productImageUrl").description("큐레이션 피드 빵집 상품 이미지 Url"),
						fieldWithPath("landing").optional().description("null"))
				)
			);
	}
}
