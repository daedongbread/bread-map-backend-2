package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryInfo;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.bakery;
import static com.depromeet.breadmapbackend.domain.flag.QFlag.flag;
import static com.depromeet.breadmapbackend.domain.review.QBreadReview.breadReview;
import static com.querydsl.core.types.dsl.MathExpressions.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BakeryRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<Bakery> getBakeryList(Double latitude, Double longitude, Double latitudeDelta, Double longitudeDelta) {
        return queryFactory.selectFrom(bakery)
                .where(bakery.latitude.between(latitude-latitudeDelta/2, latitude+latitudeDelta/2)
                        .and(bakery.longitude.between(longitude-longitudeDelta/2, longitude+longitudeDelta/2)))
//                .orderBy(acos(cos(radians(Expressions.constant(latitude))).multiply(cos(radians(bakery.latitude)))
//                        .multiply(cos(radians(bakery.longitude).subtract(radians(Expressions.constant(longitude)))))
//                        .add(sin(radians(Expressions.constant(latitude)))).multiply(sin(radians(bakery.latitude))))
//                        .multiply(Expressions.constant(6371000)).asc())
                .limit(20)
                .fetch();
    }
}
