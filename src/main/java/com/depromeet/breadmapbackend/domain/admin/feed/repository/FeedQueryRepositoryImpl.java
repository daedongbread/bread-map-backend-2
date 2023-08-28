package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import static com.depromeet.breadmapbackend.domain.admin.feed.domain.QFeed.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.global.converter.DateTimeParser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Feed> findAllFeedBySearch(Pageable pageable, FeedSearchRequest feedSearchRequest) {
		List<Feed> feeds = queryFactory.selectFrom(feed)
			.join(feed.admin).fetchJoin()
			.join(feed.category).fetchJoin()
			.where(afterCreateAt(feedSearchRequest.getCreatedAt()),
				containsCreateBy(feedSearchRequest.getCreateBy()),
				containsCategoryName(feedSearchRequest.getCategoryName()),
				eqActivated(feedSearchRequest.getActivated()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory.select(feed.count()).from(feed)
			.where(afterCreateAt(feedSearchRequest.getCreatedAt()),
				containsCreateBy(feedSearchRequest.getCreateBy()),
				containsCategoryName(feedSearchRequest.getCategoryName()),
				eqActivated(feedSearchRequest.getActivated()))
			.fetchOne();

		return new PageImpl<>(feeds, pageable, count);
	}

	@Override
	public List<Feed> getAllFeedForUser() {
		return queryFactory.selectFrom(feed)
			.where(feed.activated.eq(FeedStatus.POSTING)
				.and(feed.activeTime.before(LocalDateTime.now())))
			.orderBy(feed.activeTime.desc())
			.limit(10)
			.fetch();
	}

	private BooleanBuilder eqActivated(FeedStatus activated) {
		return nullSafeBuilder(() -> feed.activated.eq(activated));
	}

	private BooleanBuilder containsCategoryName(String categoryName) {
		return nullSafeBuilder(() -> feed.category.categoryName.contains(categoryName));
	}

	private BooleanBuilder afterCreateAt(String createdAt) {
		return nullSafeBuilder(() -> feed.createdAt.goe(DateTimeParser.parse(createdAt)));
	}

	// 어드민이 이름이 없어서 수정해야함
	private BooleanBuilder containsCreateBy(String createBy) {
		return nullSafeBuilder(() -> feed.admin.email.contains(createBy));
	}

	private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
		try {
			return new BooleanBuilder(f.get());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new BooleanBuilder();
		}
	}
}
