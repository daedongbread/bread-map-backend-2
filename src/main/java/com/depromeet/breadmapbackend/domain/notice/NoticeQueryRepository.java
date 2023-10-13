package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.QNotice.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {
	private final JPAQueryFactory queryFactory;
	private final Integer NOTICE_SIZE = 20;

	public Page<Notice> findNotice(User user, Long lastId, int page) {
		Pageable pageable = PageRequest.of(page, NOTICE_SIZE);

		List<Notice> content = queryFactory.selectFrom(notice)
			.where(notice.user.eq(user)) // TODO
			.orderBy(notice.createdAt.desc())
			.offset(page)
			.limit(NOTICE_SIZE)
			.fetch();

		Long count = queryFactory.select(notice.count()).from(notice)
			.where(notice.user.eq(user)) // TODO
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}
}
