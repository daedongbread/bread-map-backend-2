package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.admin.bakery.AdminBakeryFilter;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.bakery;
import static com.depromeet.breadmapbackend.domain.bakery.product.report.QProductAddReportImage.productAddReportImage;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryReportImage.bakeryReportImage;
import static com.depromeet.breadmapbackend.domain.bakery.report.QBakeryUpdateReport.bakeryUpdateReport;
import static com.depromeet.breadmapbackend.domain.review.QReview.review;

@Repository
@RequiredArgsConstructor
public class BakeryQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final int BAKERY_SIZE = 20;

    public Page<Bakery> getAdminBakeryList(List<AdminBakeryFilter> filterBy, String name, int page) {
        Pageable pageable = PageRequest.of(page, 20);

        List<Bakery> content = queryFactory.selectFrom(bakery)
                .where(adminBakeryCondition(filterBy), searchCondition(name))
                .offset((long) page * BAKERY_SIZE)
                .limit(BAKERY_SIZE)
                .fetch();

        Long count = queryFactory.select(bakery.count()).from(bakery)
                .where(adminBakeryCondition(filterBy))
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
                                    .groupBy(bakeryReportImage.bakery.id)
                                    .having(bakeryReportImage.count().gt(0L))));
                } else if (filter.equals(AdminBakeryFilter.PRODUCT_ADD_REPORT)) {
                    conditions.or(bakery.id.in(
                            JPAExpressions.select(productAddReportImage.bakery.id)
                                    .from(productAddReportImage)
                                    .groupBy(productAddReportImage.bakery.id)
                                    .having(productAddReportImage.count().gt(0L))));
                } else if (filter.equals(AdminBakeryFilter.BAKERY_UPDATE_REPORT)) {
                    conditions.or(bakery.id.in(
                            JPAExpressions.select(bakeryUpdateReport.bakery.id)
                                    .from(bakeryUpdateReport)
                                    .groupBy(bakeryUpdateReport.bakery.id)
                                    .having(bakeryUpdateReport.count().gt(0L))));
                } else if (filter.equals(AdminBakeryFilter.NEW_REVIEW)) {
                    conditions.or(bakery.id.in(
                            JPAExpressions.select(review.bakery.id)
                                    .from(review)
                                    .groupBy(review.bakery.id)
                                    .having(review.count().gt(0L))));
                } else throw new DaedongException(DaedongStatus.ADMIN_FILTER_EXCEPTION);
            }
        }
        return conditions;
    }

    private BooleanExpression searchCondition(String name) {
        if (name != null) {
            return bakery.name.contains(name);
        }
        else return null;
    }
}
