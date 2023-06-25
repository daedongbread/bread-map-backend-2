package com.depromeet.breadmapbackend.domain.bakery;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.*;
import static com.depromeet.breadmapbackend.domain.bakery.product.report.QProductAddReport.*;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryReportImage.*;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryUpdateReport.*;
import static com.depromeet.breadmapbackend.domain.review.QReview.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryFilter;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

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
				.and(bakery.longitude.between(coordinateRange.downLongitude(), coordinateRange.upLongitude())))
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
}
