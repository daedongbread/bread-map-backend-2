package com.depromeet.breadmapbackend.domain.admin.bakery.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.depromeet.breadmapbackend.domain.admin.bakery.AdminBakeryServiceImpl;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminBakeryDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.infra.properties.CustomAWSS3Properties;

@ExtendWith(MockitoExtension.class)
public class AdminBakeryServiceTest {

	@InjectMocks
	AdminBakeryServiceImpl adminBakeryService;
	@Mock
	private BakeryAddReportRepository bakeryAddReportRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private BakeryRepository bakeryRepository;
	@Mock
	private CustomAWSS3Properties customAWSS3Properties;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	private List<Bakery> bakeries;
	private List<Product> products;
	private CustomAWSS3Properties.DefaultImage defaultImage = new CustomAWSS3Properties.DefaultImage(
		"bakery default image",
		"comment default image",
		"like default image",
		"report default image",
		"flag default image",
		"user default image",
		"curation default image",
		"event default image",
		"bread add default image");

	@BeforeEach
	void setup() {

		List<String> images1 = List.of("bakery test image 1", "bakery test image 2");
		List<String> images2 = List.of("bakery test image 1");

		bakeries = List.of(
			Bakery.builder()
				.id(1L)
				.name("test bakery1")
				.latitude(37.5596080725671)
				.longitude(127.044235133983)
				.images(new ArrayList<>(images1))
				.build(),
			Bakery.builder()
				.id(2L)
				.name("test bakery2")
				.latitude(37.5596080725672)
				.longitude(127.044235133984)
				.images(new ArrayList<>(images2))
				.build()
		);

		products = List.of(Product.builder()
				.id(1L)
				.bakery(bakeries.get(0))
				.isTrue(true)
				.image("product image url")
				.price("10000")
				.productType(ProductType.BREAD)
				.build(),
			Product.builder()
				.id(2L)
				.bakery(bakeries.get(1))
				.isTrue(true)
				.image("product image url")
				.price("20000")
				.productType(ProductType.BREAD)
				.build());
	}

	@AfterEach
	void after() {
		bakeries = null;
		products = null;
	}

	@DisplayName("getBakery 테스트")
	@Test
	void 어드민_베이커리_상세조회_테스트() {
		//given
		given(bakeryRepository.findById(anyLong()))
			.willReturn(Optional.of(bakeries.get(0)));
		given(productRepository.findByBakeryAndIsTrueIsTrue(any(Bakery.class)))
			.willReturn(List.of(products.get(0)));
		given(customAWSS3Properties.getDefaultImage())
			.willReturn(defaultImage);

		//when
		AdminBakeryDto actual = adminBakeryService.getBakery(1L);

		//then
		assertThat(actual).isNotNull();
		assertThat(actual.getImages()).hasSize(bakeries.get(0).getImages().size());
		assertThat(actual).extracting(
			"name", "images"
		).containsExactly(
			bakeries.get(0).getName(), bakeries.get(0).getImages()
		);
		assertThat(actual.getProductList()).extracting(
			"productId", "image"
		).containsExactly(
			tuple(products.get(0).getId(), products.get(0).getImage())
		);

		verify(productRepository, times(1))
			.findByBakeryAndIsTrueIsTrue(bakeries.get(0));
		verify(customAWSS3Properties, times(bakeries.get(0).getImages().size()))
			.getDefaultImage();

	}

	@DisplayName("addBakery 테스트")
	@Test
	void 어드민_빵집_등록_테스트() {
		User user = User.builder().id(1L).build();
		BakeryAddReport addReport = new BakeryAddReport("test title", "testLocation", "test content", user);
		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		List<String> images = new ArrayList<>(List.of("add bakery Image1.jpg", "add bakery Image2.jpg"));
		BakeryAddRequest addRequest = BakeryAddRequest.builder()
			.reportId(1L)
			.name("newBakery")
			.images(images)
			.address("address")
			.detailedAddress("detailedAddress")
			.latitude(35.124124)
			.longitude(127.312452)
			.hours("09:00~20:00")
			.instagramURL("insta")
			.facebookURL("facebook")
			.blogURL("blog")
			.websiteURL("website")
			.phoneNumber("010-1234-5678")
			.facilityInfoList(facilityInfo)
			.status(BakeryStatus.POSTING)
			.productList(Arrays.asList(
				BakeryAddRequest.ProductAddRequest.builder()
					.productType(ProductType.BREAD).productName("testBread").price("12000")
					.image("add bakery Image.jpg").build()
			))
			.build();

		//given
		given(bakeryAddReportRepository.findBakeryReportWithPioneerById(anyLong()))
			.willReturn(Optional.of(addReport));
		given(bakeryRepository.existsByNameAndAddress(anyString(), anyString()))
			.willReturn(Boolean.FALSE);
		given(customAWSS3Properties.getCloudFront())
			.willReturn("test Cloud front");
		given(customAWSS3Properties.getDefaultImage())
			.willReturn(defaultImage);

		//when
		BakeryAddDto actual = adminBakeryService.addBakery(addRequest);

		//then
		assertThat(actual).isNotNull();

		verify(bakeryRepository, times(1))
			.existsByNameAndAddress(addRequest.getName(), addRequest.getAddress());
	}

	@DisplayName("addBakery - 이미지 0개 등록 테스트")
	@Test
	void 어드민_빵집_이미지_0개_등록_테스트() {
		User user = User.builder().id(1L).build();
		BakeryAddReport addReport = new BakeryAddReport("test title", "testLocation", "test content", user);
		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		BakeryAddRequest addRequest = BakeryAddRequest.builder()
			.reportId(1L)
			.name("newBakery")
			.images(new ArrayList<>())
			.address("address")
			.detailedAddress("detailedAddress")
			.latitude(35.124124)
			.longitude(127.312452)
			.hours("09:00~20:00")
			.instagramURL("insta")
			.facebookURL("facebook")
			.blogURL("blog")
			.websiteURL("website")
			.phoneNumber("010-1234-5678")
			.facilityInfoList(facilityInfo)
			.status(BakeryStatus.POSTING)
			.productList(Arrays.asList(
				BakeryAddRequest.ProductAddRequest.builder()
					.productType(ProductType.BREAD).productName("testBread").price("12000")
					.image("add bakery Image.jpg").build()
			))
			.build();

		//given
		given(bakeryAddReportRepository.findBakeryReportWithPioneerById(anyLong()))
			.willReturn(Optional.of(addReport));
		given(bakeryRepository.existsByNameAndAddress(anyString(), anyString()))
			.willReturn(Boolean.FALSE);
		given(customAWSS3Properties.getCloudFront())
			.willReturn("test Cloud front");
		given(customAWSS3Properties.getDefaultImage())
			.willReturn(defaultImage);

		//when
		BakeryAddDto actual = adminBakeryService.addBakery(addRequest);

		//then
		assertThat(actual).isNotNull();

		verify(bakeryRepository, times(1))
			.existsByNameAndAddress(addRequest.getName(), addRequest.getAddress());
	}

	@DisplayName("updateBakery 테스트")
	@Test
	void 어드민_빵집_수정_테스트() {

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		List<String> images = new ArrayList<>(List.of("updateImage1.jpg", "updateImage2.jpg"));
		BakeryUpdateRequest updateRequest = BakeryUpdateRequest.builder()
			.name("update Bakery")
			.images(images)
			.address("address")
			.detailedAddress("detailedAddress")
			.latitude(35.124124)
			.longitude(127.312452)
			.hours("09:00~20:00")
			.instagramURL("insta")
			.facebookURL("facebook")
			.blogURL("blog")
			.websiteURL("website")
			.phoneNumber("010-1234-5678")
			.facilityInfoList(facilityInfo)
			.status(BakeryStatus.POSTING)
			.newBreadTime("2023-08-2700:00:00")
			.checkPoint("update check point")
			.productList(Collections.singletonList(
				BakeryUpdateRequest.ProductUpdateRequest.builder()
					.productId(2L).productType(ProductType.BREAD)
					.productName("testBread").price("12000").image("tempImage.jpg").build()
			))
			.build();

		//given
		given(bakeryRepository.findById(anyLong()))
			.willReturn(Optional.of(bakeries.get(0)));
		given(productRepository.findById(anyLong()))
			.willReturn(Optional.of(products.get(1)));
		given(customAWSS3Properties.getCloudFront())
			.willReturn("test Cloud front");
		given(customAWSS3Properties.getDefaultImage())
			.willReturn(defaultImage);

		assertThat(bakeries.get(0)).extracting(
			"newBreadTime", "checkPoint"
		).containsOnly(
			null, null
		);

		//when
		adminBakeryService.updateBakery(bakeries.get(0).getId(), updateRequest);

		//then
		assertThat(bakeries.get(0).getProductList()).hasSize(1);
		assertThat(bakeries.get(0).getImages()).hasSize(2);
		assertThat(bakeries.get(0)).extracting(
			"newBreadTime", "checkPoint"
		).containsOnly(
			"2023-08-2700:00:00", "update check point"
		);

	}

	@DisplayName("updateBakery 테스트 - 수정 이미지가 0개일 경우 기본 이미지 1개를 삽입한다")
	@Test
	void 어드민_빵집_이미지_0개_수정_테스트() {

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		BakeryUpdateRequest updateRequest = BakeryUpdateRequest.builder()
			.name("update Bakery")
			.images(new ArrayList<>())
			.address("address")
			.detailedAddress("detailedAddress")
			.latitude(35.124124)
			.longitude(127.312452)
			.hours("09:00~20:00")
			.instagramURL("insta")
			.facebookURL("facebook")
			.blogURL("blog")
			.websiteURL("website")
			.phoneNumber("010-1234-5678")
			.facilityInfoList(facilityInfo)
			.status(BakeryStatus.POSTING)
			.productList(Collections.singletonList(
				BakeryUpdateRequest.ProductUpdateRequest.builder()
					.productId(2L).productType(ProductType.BREAD)
					.productName("testBread").price("12000").image("tempImage.jpg").build()
			))
			.build();
		//given
		given(bakeryRepository.findById(anyLong()))
			.willReturn(Optional.of(bakeries.get(0)));
		given(productRepository.findById(anyLong()))
			.willReturn(Optional.of(products.get(1)));
		given(customAWSS3Properties.getCloudFront())
			.willReturn("test Cloud front");
		given(customAWSS3Properties.getDefaultImage())
			.willReturn(defaultImage);
		//when
		adminBakeryService.updateBakery(bakeries.get(0).getId(), updateRequest);

		//then
		assertThat(bakeries.get(0).getImages()).hasSize(1);
		assertThat(bakeries.get(0).getImages().get(0)).contains(
			customAWSS3Properties.getCloudFront() + "/" + customAWSS3Properties.getDefaultImage().getBakery());
	}
}
