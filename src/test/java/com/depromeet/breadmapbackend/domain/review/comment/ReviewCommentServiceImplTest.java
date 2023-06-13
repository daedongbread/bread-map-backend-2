package com.depromeet.breadmapbackend.domain.review.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentRequest;

class ReviewCommentServiceImplTest extends ReviewCommentServiceTest {

	@Autowired
	private ReviewCommentServiceImpl sut;

	@Test
	@Sql("classpath:review-comment-test-data.sql")
	void 리뷰_댓글_성공_테스트() throws Exception {
		//given
		final String oAuthId = "APPLE_111";
		final Long reviewId = 111L;
		final ReviewCommentRequest request =
			ReviewCommentRequest.builder()
				.content("댓글 내용")
				.parentCommentId(0L)
				.build();
		//when
		sut.addReviewComment(oAuthId, reviewId, request);
		//then
		assertReviewComment(oAuthId, reviewId);
	}

	@Test
	@Sql("classpath:review-comment-test-data.sql")
	void 리뷰_대댓글_성공_테스트() throws Exception {
		//given
		final String oAuthId = "APPLE_111";
		final Long reviewId = 111L;
		final Long parentId = 113L;
		final ReviewCommentRequest request =
			ReviewCommentRequest.builder()
				.content("대댓글 내용")
				.parentCommentId(113L)
				.build();
		//when
		sut.addReviewComment(oAuthId, reviewId, request);
		//then
		assertReReviewComment(oAuthId, reviewId, parentId);
	}

	private void assertReReviewComment(final String oAuthId, final Long reviewId, final Long parentId) {
		final List<ReviewComment> resultList = em.createQuery(
				"select r "
					+ "from ReviewComment r "
					+ "where r.user.oAuthInfo.oAuthId = :oAuthId "
					+ "and r.review.id = :reviewId "
					+ "and r.parent.id = :parentId ", ReviewComment.class)
			.setParameter("oAuthId", oAuthId)
			.setParameter("reviewId", reviewId)
			.setParameter("parentId", parentId)
			.getResultList();
		assertThat(resultList).hasSize(1);
		assertThat(resultList.get(0).getContent()).isEqualTo("대댓글 내용");
	}

	private void assertReviewComment(final String oAuthId, final Long reviewId) {
		final List<ReviewComment> resultList = em.createQuery(
				"select r "
					+ "from ReviewComment r "
					+ "where r.user.oAuthInfo.oAuthId = :oAuthId "
					+ "and r.review.id = :reviewId ", ReviewComment.class)
			.setParameter("oAuthId", oAuthId)
			.setParameter("reviewId", reviewId)
			.getResultList();
		assertThat(resultList).hasSize(1);
		assertThat(resultList.get(0).getContent()).isEqualTo("댓글 내용");
	}
}