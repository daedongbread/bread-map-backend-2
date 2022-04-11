package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.QBakery;
import com.depromeet.breadmapbackend.domain.flag.QFlag;
import com.depromeet.breadmapbackend.domain.review.QBreadReview;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.SimpleReviewDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.bakery;
import static com.depromeet.breadmapbackend.domain.flag.QFlag.flag;
import static com.depromeet.breadmapbackend.domain.review.QBreadReview.breadReview;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BakeryRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<BakeryCardDto> getBakeryList(Double latitude, Double longitude, Double height, Double width) {
        return queryFactory.select(Projections.fields(BakeryCardDto.class,
                bakery.image,
                bakery.name,
                ExpressionUtils.as(JPAExpressions
                        .select(flag.count().intValue())
                        .from(flag)
                        .where(flag.bakery.id.eq(bakery.id)), "flagNum"),
                bakery.rating,
                ExpressionUtils.as(JPAExpressions
                        .select(breadReview.count().intValue())
                        .from(breadReview)
                        .where(breadReview.bakery.id.eq(bakery.id)), "reviewNum")))
//                ExpressionUtils.as(JPAExpressions
//                        .select(Projections.fields(SimpleReviewDto.class,
//                                breadReview.id,
//                                breadReview.content))
//                        .from(breadReview)
//                        .where(breadReview.bakery.id.eq(bakery.id))
//                        .limit(5), "simpleReviewList")))
                .from(bakery)
                .where(bakery.latitude.between(latitude-width, latitude+width)
                        .and(bakery.longitude.between(longitude-height, longitude+height)))
                .leftJoin(breadReview).on(breadReview.bakery.eq(bakery))
                .limit(20)
                .fetch();
    }
}
