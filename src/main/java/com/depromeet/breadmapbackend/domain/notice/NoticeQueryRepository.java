package com.depromeet.breadmapbackend.domain.notice;

import static com.depromeet.breadmapbackend.domain.notice.QNotice.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {
	private final JPAQueryFactory queryFactory;
	private final Integer NOTICE_SIZE = 20;

	public Page<Notice> findNotice(User user, NoticeDayType type, Long lastId, int page) {
		Pageable pageable = PageRequest.of(page, NOTICE_SIZE);

		List<Notice> content = queryFactory.selectFrom(notice)
			.where(notice.user.eq(user),
				dateTimeCondition(type, lastId, page))
			.orderBy(notice.createdAt.desc())
			.offset(page)
			.limit(NOTICE_SIZE)
			.fetch();

		Long count = queryFactory.select(notice.count()).from(notice)
			.where(notice.user.eq(user),
				dateTimeCondition(type, lastId, page))
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanExpression dateTimeCondition(NoticeDayType type, Long lastId, int page) {
		BooleanExpression booleanExpression;
		if (type.equals(NoticeDayType.TODAY)) {
			booleanExpression = notice.createdAt.after(LocalDateTime.now().with(LocalTime.MIN));
		} else if (type.equals(NoticeDayType.WEEK)) {
			booleanExpression = notice.createdAt.between(
				LocalDateTime.now().with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
				LocalDateTime.now().minusDays(1).with(LocalTime.MAX));
		} else if (type.equals(NoticeDayType.BEFORE)) {
			booleanExpression = notice.createdAt.before(
				LocalDateTime.now().with(LocalTime.MAX).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)));
		} else
			throw new DaedongException(DaedongStatus.NOTICE_DAY_TYPE_EXCEPTION);

		if (lastId == null && page == 0)
			return booleanExpression;
		else if (lastId != null && page != 0)
			return booleanExpression.and(notice.id.lt(lastId));
		else
			throw new DaedongException(DaedongStatus.NOTICE_PAGE_EXCEPTION);
	}
}
