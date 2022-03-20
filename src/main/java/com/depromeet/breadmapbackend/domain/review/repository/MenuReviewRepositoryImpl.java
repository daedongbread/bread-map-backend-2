package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;
import com.depromeet.breadmapbackend.domain.review.MenuReview;

public class MenuReviewRepositoryImpl extends MyQuerydslRepositorySupport implements MenuReviewRepositoryCustom {

    public MenuReviewRepositoryImpl() {super(MenuReview.class);}


}
