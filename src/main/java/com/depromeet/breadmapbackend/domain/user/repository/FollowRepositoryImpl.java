package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;
import com.depromeet.breadmapbackend.domain.user.Follow;

public class FollowRepositoryImpl extends MyQuerydslRepositorySupport implements FollowRepositoryCustom {

    public FollowRepositoryImpl() {super(Follow.class);}

}
