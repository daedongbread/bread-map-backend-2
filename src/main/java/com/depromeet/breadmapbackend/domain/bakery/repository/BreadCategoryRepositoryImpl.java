package com.depromeet.breadmapbackend.domain.bakery.repository;

import com.depromeet.breadmapbackend.domain.bakery.BreadCategory;
import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class BreadCategoryRepositoryImpl extends MyQuerydslRepositorySupport implements BreadCategoryRepositoryCustom {

    public BreadCategoryRepositoryImpl() {super(BreadCategory.class);}


}
