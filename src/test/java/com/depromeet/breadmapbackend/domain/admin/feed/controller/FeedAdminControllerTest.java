package com.depromeet.breadmapbackend.domain.admin.feed.controller;

import static com.google.protobuf.FieldType.*;
import static org.springframework.asm.Type.*;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
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
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

public class FeedAdminControllerTest extends ControllerTest {
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
			.activeTime("9999-01-01T00:00:00")
			.build();

		CommonFeedRequestDto commonLanding = CommonFeedRequestDto.builder()
			.subTitle("랜딩 테스트 피드")
			.introduction("랜딩 테스트 피드 서론")
			.conclusion("랜딩 테스트 피드 결론")
			.categoryId(category.getId())
			.thumbnailUrl(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.activated(FeedStatus.POSTING)
			.feedType(FeedType.LANDING)
			.activeTime("9999-01-01T00:00:00")
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
	@DisplayName("피드를 등록한다 - 랜딩 피드")
	void 랜딩피드_등록_테스트() throws Exception {

		//given
		String content = objectMapper.writeValueAsString(landingRequest);

		//when
		ResultActions perform = mockMvc.perform(post("/v1/admin/feed")
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(content));

		perform.andExpect(status().isCreated());

		//then
		perform.andDo(print()).andDo(document("create-landing-feed-admin",
			preprocessRequest(prettyPrint()),
			preprocessResponse(prettyPrint()),
			requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
			requestFields(
				fieldWithPath("common.subTitle").optional().description("피드 소제목"),
				fieldWithPath("common.introduction").optional().description("피드 시작하는 말"),
				fieldWithPath("common.conclusion").optional().description("피드 끝맺음 말"),
				fieldWithPath("common.thumbnailUrl").description("피드 배너 이미지 url"),
				fieldWithPath("common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
				fieldWithPath("common.feedType").description("피드 타입(LANDING, CURATION)"),
				fieldWithPath("common.categoryId").description("카테고리 아이디"),
				fieldWithPath("common.activeTime").type(STRING).description("피드 게시 시작 날짜(yyyy-mm-ddTHH:mm:ss)"),
				fieldWithPath("curation").description("null 보내주세요"),
				fieldWithPath("landing.redirectUrl").optional().description("랜딩 RedirectUrl")),
			responseHeaders(
				headerWithName(HttpHeaders.LOCATION).description("어드민 - 피드 상세 보기 주소")
			))
		);
	}

	@Test
	@DisplayName("피드를 등록한다 - 큐레이션 피드")
	void 큐레이션피드_등록_테스트() throws Exception {

		//given
		String content = objectMapper.writeValueAsString(curationRequest);

		//when
		ResultActions perform = mockMvc.perform(post("/v1/admin/feed")
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(content));

		perform.andExpect(status().isCreated());

		//then
		perform.andDo(print()).andDo(document("create-curation-feed-admin",
			preprocessRequest(prettyPrint()),
			preprocessResponse(prettyPrint()),
			requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
			requestFields(
				fieldWithPath("common.subTitle").optional().description("피드 소제목"),
				fieldWithPath("common.introduction").optional().description("피드 시작하는 말"),
				fieldWithPath("common.conclusion").optional().description("피드 끝맺음 말"),
				fieldWithPath("common.thumbnailUrl").description("피드 배너 이미지 url"),
				fieldWithPath("common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
				fieldWithPath("common.feedType").description("피드 타입(LANDING, CURATION)"),
				fieldWithPath("common.categoryId").description("카테고리 아이디"),
				fieldWithPath("common.activeTime").type(STRING).description("피드 게시 시작 날짜(yyyy-mm-ddTHH:mm:ss)"),
				fieldWithPath("curation.[].bakeryId").optional().description("추천 빵집 ID"),
				fieldWithPath("curation.[].productId").optional().description("추천 빵집 메뉴 ID"),
				fieldWithPath("curation.[].reason").optional().description("추천 이유"),
				fieldWithPath("landing").description("null 보내주세요")),
			responseHeaders(
				headerWithName(HttpHeaders.LOCATION).description("어드민 - 피드 상세 보기 주소")
			))
		);
	}

	@Test
	@DisplayName("피드를 수정한다 - 랜딩 피드")
	void 랜딩피드_수정_테스트() throws Exception {

		//given

		CommonFeedRequestDto updateCommon = CommonFeedRequestDto.builder()
			.subTitle("업데이트된 큐레이션 테스트 피드")
			.introduction("업데이트된 큐레이션 테스트 피드 서론")
			.conclusion("업데이트된 큐레이션 테스트 피드 결론")
			.categoryId(category.getId())
			.thumbnailUrl(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.activated(FeedStatus.INACTIVATED)
			.feedType(FeedType.LANDING)
			.activeTime("2023-07-01T00:00:00")
			.build();

		LandingFeedRequestDto updateLandingDto = new LandingFeedRequestDto("/updateRedirectUrl");

		FeedRequestDto updateRequest = new FeedRequestDto(updateCommon, updateLandingDto);

		String content = objectMapper.writeValueAsString(updateRequest);

		//when
		ResultActions perform = mockMvc.perform(patch("/v1/admin/feed/{feedId}", landing.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(content));

		perform.andExpect(status().isNoContent());

		//then
		perform.andDo(print()).andDo(document("update-landing-feed-admin",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
				pathParameters(
					parameterWithName("feedId").description("피드 아이디")
				),
				requestFields(
					fieldWithPath("common.subTitle").optional().description("업데이트된 피드 소제목"),
					fieldWithPath("common.introduction").optional().description("업데이트된 피드 시작하는 말"),
					fieldWithPath("common.conclusion").optional().description("업데이트된 피드 끝맺음 말"),
					fieldWithPath("common.thumbnailUrl").description("업데이트된 피드 배너 이미지 url"),
					fieldWithPath("common.activated").description("업데이트된 피드 활성화 여부(POSTING, INACTIVATED)"),
					fieldWithPath("common.feedType").description("업데이트된 피드 타입(LANDING, CURATION)"),
					fieldWithPath("common.categoryId").description("카테고리 아이디"),
					fieldWithPath("common.activeTime").type(STRING).description("업데이트된  피드 게시 시작 날짜(yyyy-mm-ddTHH:mm:ss)"),
					fieldWithPath("curation").description("null 보내주세요"),
					fieldWithPath("landing.redirectUrl").optional().description("업데이트된 redirectURl"))
			)
		);
	}

	@Test
	@DisplayName("피드를 수정한다 - 큐레이션 피드")
	void 큐레이션피드_수정_테스트() throws Exception {

		//given

		CommonFeedRequestDto updateCommon = CommonFeedRequestDto.builder()
			.subTitle("업데이트된 큐레이션 테스트 피드")
			.introduction("업데이트된 큐레이션 테스트 피드 서론")
			.conclusion("업데이트된 큐레이션 테스트 피드 결론")
			.categoryId(category.getId())
			.thumbnailUrl(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.activated(FeedStatus.POSTING)
			.feedType(FeedType.CURATION)
			.activeTime("2023-07-01T00:00:00")
			.build();

		Bakery updateBakery = Bakery.builder()
			.address("address")
			.latitude(37.5596080751252)
			.longitude(127.044235146211)
			.facilityInfoList(Collections.singletonList(FacilityInfo.PARKING))
			.name("update bakery")
			.status(BakeryStatus.POSTING)
			.image(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg")
			.build();

		Bakery updatebakery = bakeryRepository.save(updateBakery);

		Product updateBakeryBread = Product.builder()
			.bakery(updateBakery)
			.productType(ProductType.BREAD)
			.name("update bakery bread")
			.price("20000")
			.image(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg")
			.build();

		Product updateProduct = productRepository.save(updateBakeryBread);

		CurationFeedRequestDto updateCurationDto = CurationFeedRequestDto.builder()
			.bakeryId(updatebakery.getId())
			.productId(updateProduct.getId())
			.reason("update Reason")
			.build();

		FeedRequestDto updateRequest = new FeedRequestDto(updateCommon, List.of(updateCurationDto));

		String content = objectMapper.writeValueAsString(updateRequest);

		//when
		ResultActions perform = mockMvc.perform(patch("/v1/admin/feed/{feedId}", curation.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(content));

		perform.andExpect(status().isNoContent());

		//then
		perform.andDo(print()).andDo(document("update-curation-feed-admin",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
				pathParameters(
					parameterWithName("feedId").description("피드 아이디")
				),
				requestFields(
					fieldWithPath("common.subTitle").optional().description("업데이트된 피드 소제목"),
					fieldWithPath("common.introduction").optional().description("업데이트된 피드시작하는 말"),
					fieldWithPath("common.conclusion").optional().description("업데이트된 피드 끝맺음 말"),
					fieldWithPath("common.thumbnailUrl").description("업데이트된 피드 배너 이미지 url"),
					fieldWithPath("common.activated").description("업데이트된 피드 활성화 여부(POSTING, INACTIVATED)"),
					fieldWithPath("common.feedType").description("업데이트된 피드 타입(LANDING, CURATION)"),
					fieldWithPath("common.categoryId").description("카테고리 아이디"),
					fieldWithPath("common.activeTime").type(STRING).description("업데이트된 피드 게시 시작 날짜(yyyy-mm-ddTHH:mm:ss)"),
					fieldWithPath("curation.[].bakeryId").optional().description("업데이트된 피드 추천 빵집 ID"),
					fieldWithPath("curation.[].productId").optional().description("업데이트된 피드 추천 빵집 메뉴 ID"),
					fieldWithPath("curation.[].reason").optional().description("업데이트된 피드 추천 빵집 추천 이유"),
					fieldWithPath("landing").description("null 보내주세요"))
			)
		);
	}

	@Test
	@DisplayName("전체 피드를 조회한다 - 관리자용")
	void 피드_전체_조회_테스트_관리자() throws Exception {

		//given
		List<Feed> feedList = List.of(landing, curation);

		PageImpl<Feed> page = new PageImpl<>(feedList);

		FeedResponseForAdmin content = FeedResponseForAdmin.builder()
			.feeds(feedList)
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.build();

		ApiResponse<FeedResponseForAdmin> res = new ApiResponse<>(content);

		String response = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/admin/feed/all")
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.queryParam("createdAt", "2023-01-01T00:00")
			.queryParam("activated", "POSTING")
			.queryParam("page", "0")
			.queryParam("size", "20"));

		perform.andExpect(status().isOk());

		//then
		perform.andDo(print()).
			andDo(document("findall-feed-admin",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					requestParameters(
						parameterWithName("createdAt").optional().description("등록 일자"),
						parameterWithName("createBy").optional().description("작성자 이메일"),
						parameterWithName("activated").optional().description("게시 여부(POSTING, INACTIVATED)"),
						parameterWithName("categoryName").optional().description("카테고리 이름"),
						parameterWithName("page").optional().description("페이지(0부터시작)"),
						parameterWithName("size").optional().description("한 페이지에 출력할 개수(default 20)")),
					responseFields(
						fieldWithPath("data.totalPages").description("전체 페이지 수"),
						fieldWithPath("data.totalElements").description("전체 데이터 개수"),
						fieldWithPath("data.contents.[].feedId").description("피드 번호"),
						fieldWithPath("data.contents.[].feedTitle").description("피드 제목"),
						fieldWithPath("data.contents.[].authorName").description("피드 작성자 이메일"),
						fieldWithPath("data.contents.[].createdAt").description("피드 등록일자"),
						fieldWithPath("data.contents.[].categoryName").description("피드 카테고리 이름"),
						fieldWithPath("data.contents.[].isActive").description("피드 활성화 여부")
					)
				)
			);
	}

	@Test
	@DisplayName("랜딩 피드 상세 조회 - 관리자")
	void 랜딩_피드_상세_조회_관리자() throws Exception {

		//given
		FeedResponseDto response = FeedResponseDto.builder()
			.common(FeedAssembler.toCommonDto(landing))
			.landing(FeedAssembler.toLandingDto(landing))
			.build();

		ApiResponse<FeedResponseDto> res = new ApiResponse<>(response);
		String content = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/admin/feed/{feedId}", landing.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.param("feedType", "landing"));

		perform.andExpect(status().isOk())
			.andExpect(content().string(content));

		//then
		perform.andDo(print()).
			andDo(document("find-detail-landing-feed-admin",
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
						fieldWithPath("data.common.feedId").description("피드 소제목"),
						fieldWithPath("data.common.subTitle").description("피드 소제목"),
						fieldWithPath("data.common.introduction").description("피드 시작하는 말"),
						fieldWithPath("data.common.conclusion").description("피드 끝맺음 말"),
						fieldWithPath("data.common.thumbnailUrl").description("피드 배너 이미지 url"),
						fieldWithPath("data.common.categoryName").description("카테고리 이름"),
						fieldWithPath("data.common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
						fieldWithPath("data.common.feedType").description("피드 타입(LANDING, CURATION)"),
						fieldWithPath("data.common.activateTime").description("피드 게시 시작 날짜"),
						fieldWithPath("data.curation").optional().description("null"),
						fieldWithPath("data.landing.redirectUrl").description("redirectURl"),
						fieldWithPath("data.likeCounts").description("현재 피드 좋아요 개수"))
				)
			);
	}

	@Test
	@DisplayName("큐레이션 피드 상세 조회 - 관리자")
	void 큐레이션_피드_상세_조회_관리자() throws Exception {

		//given
		List<Product> products = productRepository.findByIdIn(curation.getBakeries().getProductIdList());
		List<Bakery> bakeries = products.stream().map(Product::getBakery).collect(Collectors.toList());

		FeedResponseDto response = FeedResponseDto.builder()
			.common(FeedAssembler.toCommonDto(curation))
			.curation(FeedAssembler.toCurationDto(bakeries, products))
			.likeCounts(curation.getLikeCount())
			.build();

		ApiResponse<FeedResponseDto> res = new ApiResponse<>(response);
		String content = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/admin/feed/{feedId}", curation.getId())
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.param("feedType", "curation"));

		perform.andExpect(status().isOk())
			.andExpect(content().string(content));

		//then
		perform.andDo(print()).
			andDo(document("find-detail-curation-feed-admin",
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
						fieldWithPath("data.common.feedId").description("피드 소제목"),
						fieldWithPath("data.common.subTitle").description("피드 소제목"),
						fieldWithPath("data.common.introduction").description("피드 시작하는 말"),
						fieldWithPath("data.common.conclusion").description("피드 끝맺음 말"),
						fieldWithPath("data.common.thumbnailUrl").description("피드 배너 이미지 url"),
						fieldWithPath("data.common.categoryName").description("카테고리 이름"),
						fieldWithPath("data.common.activated").description("피드 활성화 여부(POSTING, INACTIVATED)"),
						fieldWithPath("data.common.feedType").description("피드 타입(LANDING, CURATION)"),
						fieldWithPath("data.common.activateTime").description("피드 게시 시작 날짜"),
						fieldWithPath("data.curation.[].bakeryId").description("큐레이션 피드 빵집 ID"),
						fieldWithPath("data.curation.[].bakeryName").description("큐레이션 피드 빵집 이름"),
						fieldWithPath("data.curation.[].bakeryAddress").description("큐레이션 피드 빵집 주소"),
						fieldWithPath("data.curation.[].openingHours").description("큐레이션 피드 빵집 오픈시각"),
						fieldWithPath("data.curation.[].bakeryImageUrl").description("큐레이션 피드 빵집 이미지 Url"),
						fieldWithPath("data.curation.[].checkPoint").description("큐레이션 피드 빵집 체크포인트"),
						fieldWithPath("data.curation.[].newBreadTime").description("큐레이션 피드 빵집 갓군빵 나오는 시간"),
						fieldWithPath("data.curation.[].address").description("큐레이션 피드 빵집 주소"),
						fieldWithPath("data.curation.[].detailedAddress").description("큐레이션 피드 빵집 상세주소"),
						fieldWithPath("data.curation.[].websiteURL").description("큐레이션 피드 빵집 웹사이트 Url"),
						fieldWithPath("data.curation.[].instagramURL").description("큐레이션 피드 빵집 인스타 Url"),
						fieldWithPath("data.curation.[].facebookURL").description("큐레이션 피드 빵집 페이스북 Url"),
						fieldWithPath("data.curation.[].blogURL").description("큐레이션 피드 빵집 상품 블로그 Url"),
						fieldWithPath("data.curation.[].facilityInfo").type(ARRAY).description("큐레이션 피드 빵집 태그 리스트"),
						fieldWithPath("data.curation.[].phoneNumber").description("큐레이션 피드 빵집 전하번호"),
						fieldWithPath("data.curation.[].productId").description("큐레이션 피드 빵집 상품 ID"),
						fieldWithPath("data.curation.[].productName").description("큐레이션 피드 빵집 상품 이름"),
						fieldWithPath("data.curation.[].productPrice").description("큐레이션 피드 빵집 상품 가격"),
						fieldWithPath("data.curation.[].productImageUrl").description("큐레이션 피드 빵집 상품 이미지 Url"),
						fieldWithPath("data.landing").optional().description("null"),
						fieldWithPath("data.likeCounts").description("현재 피드 좋아요 개수"))
				)
			);
	}
}