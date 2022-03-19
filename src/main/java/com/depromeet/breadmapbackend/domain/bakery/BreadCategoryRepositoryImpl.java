package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class BreadCategoryRepositoryImpl extends MyQuerydslRepositorySupport implements BreadCategoryRepositoryCustom {

    public BreadCategoryRepositoryImpl() {super(BreadCategory.class);}


}
