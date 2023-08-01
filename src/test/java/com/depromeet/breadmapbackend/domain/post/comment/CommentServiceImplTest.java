package com.depromeet.breadmapbackend.domain.post.comment;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLike;

/**
 * CommentServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
class CommentServiceImplTest extends CommentServiceTest {

	@Autowired
	private CommentServiceImpl sut;

	@Autowired
	private EntityManager em;

	@Test
	@Sql("classpath:comment-test-data.sql")
	void 댓글_등록() throws Exception {
		//given
		final Long userId = 111L;
		final Command command = new Command(
			222L,
			"댓글 내용9090909",
			true,
			PostTopic.FREE_TALK,
			0L,
			0L
		);
		//when
		final Comment result = sut.register(command, userId);

		//then
		final Comment savedComment = em.find(Comment.class, result.getId());
		assertThat(savedComment.getContent()).isEqualTo(command.content());
		assertThat(savedComment.getStatus()).isEqualTo(CommentStatus.ACTIVE);
	}

	@Test
	@Sql("classpath:comment-test-data.sql")
	void 댓글_삭제() throws Exception {
		//given
		final Long userId = 111L;
		final Long commentId = 111L;
		//when
		sut.deleteComment(commentId, userId);

		//then
		final Comment deletedComment = em.find(Comment.class, commentId);
		assertThat(deletedComment.getStatus()).isEqualTo(CommentStatus.DELETED);
	}

	@Test
	@Sql("classpath:comment-test-data.sql")
	void 댓글_조회() throws Exception {
		//given
		final Long userId = 111L;
		final Long postId = 222L;
		final PostTopic topic = PostTopic.BREAD_STORY;
		//when
		final Page<CommentInfo.Response> result = sut.findComment(postId, topic, userId, 0);
		//then
		final List<CommentInfo.Response> comments = result.getContent();
		assertThat(comments).hasSize(10);
		assertThat(comments.get(0).content()).isEqualTo(CommentResponseStatus.BLOCKED_BY_ADMIN.getContent());
		assertThat(comments.get(0).id()).isEqualTo(122L);
		assertThat(comments.get(1).responseStatus()).isEqualTo(CommentResponseStatus.ACTIVE);
		assertThat(comments.get(1).content()).isEqualTo("content");
		assertThat(comments.get(4).content()).isEqualTo("content 2");
		assertThat(comments.get(5).content()).isEqualTo(CommentResponseStatus.DELETED.getContent());

	}

	@Test
	@Sql("classpath:comment-test-data.sql")
	void 댓글_수정() throws Exception {
		//given
		final UpdateCommand command = new UpdateCommand(
			111L,
			"수정된 댓글 내용"
		);
		//when
		sut.updateComment(command, 111L);

		//then
		final Comment updatedComment = em.find(Comment.class, command.commentId());
		assertThat(updatedComment.getContent()).isEqualTo(command.content());
	}

	@Test
	@Sql("classpath:comment-test-data.sql")
	void 댓글_좋아요_토글() throws Exception {
		//given
		final Long userId = 111L;
		final Long commentId = 111L;
		//when
		sut.toggleLike(commentId, userId);

		//then
		final CommentLike result = em.createQuery(
				"select cl from CommentLike cl where userId =:userId and comment.id =:commentId",
				CommentLike.class)
			.setParameter("userId", userId)
			.setParameter("commentId", commentId)
			.getSingleResult();

		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getComment().getId()).isEqualTo(commentId);

		//when
		final int reuslt2 = sut.toggleLike(commentId, userId);

		//then
		assertThat(reuslt2).isEqualTo(0);
		assertThatThrownBy(() -> em.createQuery(
				"select cl from CommentLike cl where userId =:userId and comment.id =:commentId",
				CommentLike.class)
			.setParameter("userId", userId)
			.setParameter("commentId", commentId)
			.getSingleResult()
		).isInstanceOf(NoResultException.class);
	}

}