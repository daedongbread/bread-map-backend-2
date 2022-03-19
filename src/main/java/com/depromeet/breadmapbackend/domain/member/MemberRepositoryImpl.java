package com.depromeet.breadmapbackend.domain.member;

import com.depromeet.breadmapbackend.domain.common.MyQuerydslRepositorySupport;

public class MemberRepositoryImpl extends MyQuerydslRepositorySupport implements MemberRepositoryCustom {

    public MemberRepositoryImpl() {super(Member.class);}

}
