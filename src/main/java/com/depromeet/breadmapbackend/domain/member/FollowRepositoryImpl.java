package com.depromeet.breadmapbackend.domain.member;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class FollowRepositoryImpl extends MyQuerydslRepositorySupport implements FollowRepositoryCustom {

    public FollowRepositoryImpl() {super(Follow.class);}

}
