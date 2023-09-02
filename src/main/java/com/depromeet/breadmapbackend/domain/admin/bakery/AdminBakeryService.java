package com.depromeet.breadmapbackend.domain.admin.bakery;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminBakeryAlarmBar;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminBakeryDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminBakeryIsNewDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminImageBarDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminImageDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminImageRegisterRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminSimpleBakeryDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryLocationDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryProductsDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryUpdateReportDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.NewReviewDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.ProductAddReportDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryImageType;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface AdminBakeryService {
	AdminBakeryAlarmBar getBakeryAlarmBar();

	PageResponseDto<AdminSimpleBakeryDto> getBakeryList(List<AdminBakeryFilter> filterBy, String name, int page);

	AdminBakeryDto getBakery(Long bakeryId);

	//    PageResponseDto<AdminSimpleBakeryDto> searchBakeryList(String name, int page);
	BakeryLocationDto getBakeryLatitudeLongitude(String address);

	BakeryAddDto addBakery(BakeryAddRequest request);

	void updateBakery(Long bakeryId, BakeryUpdateRequest request);

	void deleteProduct(Long bakeryId, Long productId);

	AdminBakeryIsNewDto getAdminBakeryIsNewBar(Long bakeryId);

	AdminImageBarDto getAdminImageBar(Long bakeryId);

	PageResponseDto<AdminImageDto> getAdminImages(Long bakeryId, AdminBakeryImageType imageType, int page);

	void deleteAdminImage(Long bakeryId, AdminBakeryImageType type, Long imageId);

	PageResponseDto<ProductAddReportDto> getProductAddReports(Long bakeryId, int page);

	void registerProductAddImage(Long bakeryId, Long reportId, AdminImageRegisterRequest request);

	void deleteProductAddReport(Long bakeryId, Long reportId);

	PageResponseDto<BakeryUpdateReportDto> getBakeryUpdateReports(Long bakeryId, int page);

	void changeBakeryUpdateReport(Long bakeryId, Long reportId);

	void deleteBakeryUpdateReport(Long bakeryId, Long reportId);

	PageResponseDto<NewReviewDto> getNewReviews(Long bakeryId, int page);

	void hideNewReview(Long bakeryId, Long reviewId);

	void registerNewReviewImage(Long bakeryId, Long reviewId, AdminImageRegisterRequest request);

	void deleteReview(Long bakeryId, Long reviewId);

	List<BakeryProductsDto> getBakeryProducts(Long bakeryId);
	//    void deleteBakery(Long bakeryId);
}
