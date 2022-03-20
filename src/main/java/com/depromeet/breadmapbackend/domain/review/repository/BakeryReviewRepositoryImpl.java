package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;
import com.depromeet.breadmapbackend.domain.review.BakeryReview;

public class BakeryReviewRepositoryImpl extends MyQuerydslRepositorySupport implements BakeryReviewRepositoryCustom {

    public BakeryReviewRepositoryImpl() {super(BakeryReview.class);}

}
