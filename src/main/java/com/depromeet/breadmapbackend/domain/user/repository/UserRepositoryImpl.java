package com.depromeet.breadmapbackend.domain.user.repository;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;
import com.depromeet.breadmapbackend.domain.user.User;

public class UserRepositoryImpl extends MyQuerydslRepositorySupport implements UserRepositoryCustom {

    public UserRepositoryImpl() {super(User.class);}

}
