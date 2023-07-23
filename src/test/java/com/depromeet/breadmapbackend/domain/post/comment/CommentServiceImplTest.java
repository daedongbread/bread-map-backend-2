package com.depromeet.breadmapbackend.domain.post.comment;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;

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
			"댓글 내용",
			true,
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
		
		//when

		//then
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

}