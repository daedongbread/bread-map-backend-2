package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBase;
import com.depromeet.breadmapbackend.domain.bakery.dto.CoordinateRange;
import com.depromeet.breadmapbackend.domain.bakery.dto.NewBakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BakeryLoadData;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.BreadLoadData;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.bakery;
import static com.depromeet.breadmapbackend.domain.bakery.product.QProduct.product;
import static com.depromeet.breadmapbackend.domain.bakery.product.report.QProductAddReport.productAddReport;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryAddReport.bakeryAddReport;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryReportImage.bakeryReportImage;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryUpdateReport.bakeryUpdateReport;
import static com.depromeet.breadmapbackend.domain.bakery.view.QBakeryView.bakeryView;
import static com.depromeet.breadmapbackend.domain.flag.QFlagBakery.flagBakery;
import static com.depromeet.breadmapbackend.domain.review.QReview.review;
import static com.depromeet.breadmapbackend.domain.review.QReviewProductRating.reviewProductRating;
import static com.depromeet.breadmapbackend.domain.user.QUser.user;
import static com.depromeet.breadmapbackend.domain.user.follow.QFollow.follow;

@Repository
@RequiredArgsConstructor
public class BakeryQueryRepository {
	private static final int PAGE_SIZE = 20;
	private static final int BAKERY_SIZE = PAGE_SIZE;
	private final JPAQueryFactory queryFactory;

	public List<Bakery> findTop20BakeriesByCoordinateRange(final CoordinateRange coordinateRange) {
		return queryFactory
			.select(bakery)
			.from(bakery)
			.where(bakery.latitude.between(coordinateRange.leftLatitude(), coordinateRange.rightLatitude())
				.and(bakery.longitude.between(coordinateRange.downLongitude(), coordinateRange.upLongitude()))
				.and(bakery.status.eq(BakeryStatus.POSTING)))
			.limit(BAKERY_SIZE)
			.fetch();
	}

	public Page<Bakery> getAdminBakeryList(List<AdminBakeryFilter> filterBy, String name, int page) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);

		List<Bakery> content = queryFactory.selectFrom(bakery)
			.where(adminBakeryCondition(filterBy), searchCondition(name))
			.offset((long)page * BAKERY_SIZE)
			.limit(BAKERY_SIZE)
			.fetch();

		Long count = queryFactory.select(bakery.count()).from(bakery)
			.where(adminBakeryCondition(filterBy), searchCondition(name))
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanBuilder adminBakeryCondition(List<AdminBakeryFilter> filterBy) {
		BooleanBuilder conditions = new BooleanBuilder();
		if (filterBy != null) {
			for (AdminBakeryFilter filter : filterBy) {
				if (filter.equals(AdminBakeryFilter.BAKERY_REPORT_IMAGE)) {
					conditions.or(bakery.id.in(
						JPAExpressions.select(bakeryReportImage.bakery.id)
							.from(bakeryReportImage)
							.where(bakeryReportImage.isNew.isTrue())
							.groupBy(bakeryReportImage.bakery.id)
							.having(bakeryReportImage.count().gt(0L))));
				} else if (filter.equals(AdminBakeryFilter.PRODUCT_ADD_REPORT)) {
					conditions.or(bakery.id.in(
						JPAExpressions.select(productAddReport.bakery.id)
							.from(productAddReport)
							.where(productAddReport.isNew.isTrue())
							.groupBy(productAddReport.bakery.id)
							.having(productAddReport.count().gt(0L))));
				} else if (filter.equals(AdminBakeryFilter.BAKERY_UPDATE_REPORT)) {
					conditions.or(bakery.id.in(
						JPAExpressions.select(bakeryUpdateReport.bakery.id)
							.from(bakeryUpdateReport)
							.where(bakeryUpdateReport.isNew.isTrue())
							.groupBy(bakeryUpdateReport.bakery.id)
							.having(bakeryUpdateReport.count().gt(0L))));
				} else if (filter.equals(AdminBakeryFilter.NEW_REVIEW)) {
					conditions.or(bakery.id.in(
						JPAExpressions.select(review.bakery.id)
							.from(review)
							.where(review.isNew.isTrue())
							.groupBy(review.bakery.id)
							.having(review.count().gt(0L))));
				} else
					throw new DaedongException(DaedongStatus.ADMIN_FILTER_EXCEPTION);
			}
		}
		return conditions;
	}

	private BooleanExpression searchCondition(String name) {
		if (name != null) {
			return bakery.name.contains(name);
		} else
			return null;
	}

	public List<BakeryScoreBase> getBakeriesScoreFactors(final LocalDate date) {
		return queryFactory
			.select(Projections.constructor(
					BakeryScoreBase.class
					, bakery.as("bakery")
					// , avgRatingSubQuery(date)
					, countFlagBakerySubQuery(date)
					, bakeryView.viewCount.sum().coalesce(0L)
				)
			)
			.from(bakery)
			.leftJoin(bakeryView)
			.on(bakery.id.eq(bakeryView.bakeryId))
			.on(bakeryView.viewDate.between(date.minusDays(7), date))
			.where(bakery.status.eq(BakeryStatus.POSTING))
			.groupBy(bakery.id)
			.fetch();

	}

	public List<NewBakeryDto> findBakeryWithPioneerByCreatedAtDesc(final Long userId, final int newBakeryListSize) {
		return queryFactory
			.select(Projections.constructor(NewBakeryDto.class
				, bakery
				, new CaseBuilder()
					.when(flagBakery.count().eq(0L)).then(false)
					.otherwise(true)
				, new CaseBuilder()
					.when(follow.isNull()).then(false)
					.otherwise(true)
			))
			.from(bakery)
			.leftJoin(bakery.bakeryAddReport, bakeryAddReport).fetchJoin()
			.leftJoin(bakeryAddReport.user, user).fetchJoin()
			.leftJoin(follow).on(follow.fromUser.id.eq(userId)
				.and(follow.toUser.id.eq(bakery.bakeryAddReport.user.id)))
			.leftJoin(flagBakery).on(flagBakery.bakery.eq(bakery)
				.and(flagBakery.user.id.eq(userId)))
			.where(bakery.status.eq(BakeryStatus.POSTING)
				.and(bakery.bakeryAddReport.isNotNull()))
			.groupBy(bakery.id, bakery.createdAt)
			.orderBy(bakery.createdAt.desc())
			.limit(newBakeryListSize)
			.fetch();
	}

	private JPQLQuery<Long> countFlagBakerySubQuery(LocalDate startDate) {
		return JPAExpressions.select(flagBakery.id.count().coalesce(0L))
			.from(flagBakery)
			.where(bakery.id.eq(flagBakery.bakery.id)
				.and(flagBakery.createdAt.between(
					startDate.minusDays(7).atStartOfDay(),
					startDate.atTime(LocalTime.MAX))));
	}

	public List<BakeryLoadData> bakeryLoadEntireDataJPQLQuery() {
		return queryFactory
				.select(Projections.constructor(BakeryLoadData.class
						, bakery.id
						, bakery.name
						, bakery.address
						, bakery.longitude
						, bakery.latitude
				))
				.from(bakery)
				.fetch();
	}

	public List<BreadLoadData> breadLoadEntireDataJPQLQuery() {
		return queryFactory
				.select(Projections.constructor(BreadLoadData.class
						, product.id
						, product.name
						, bakery.id
						, bakery.name
						, bakery.address
						, bakery.longitude
						, bakery.latitude
				))
				.from(bakery)
				.innerJoin(product)
				.on(bakery.id.eq(product.bakery.id))
				.where((product.productType.eq(ProductType.BREAD)))
				.fetch();
	}

}
