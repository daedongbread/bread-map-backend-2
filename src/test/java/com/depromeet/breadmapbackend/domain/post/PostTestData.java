package com.depromeet.breadmapbackend.domain.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * PostTestData
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class PostTestData {

	static void preparePostLikeData(final Connection connection) throws SQLException {
		final String postListSql = """
			insert into post_like(	id,post_id,user_id,created_at)values
			(222, 222, 111, '%s'),
			(224, 223, 111, '%s'),
			(223, 222, 112, '%s');
			""".replaceAll("%s", LocalDateTime.now().toString());
		PreparedStatement statement = connection.prepareStatement(postListSql);
		statement.executeUpdate();
	}

	static void prepareReviewCommentData(final Connection connection) throws SQLException {
		final String reviewCommentSql = String.format("""				
			insert into review_comment (id, created_at, modified_at, content, is_delete, parent_id, review_id, user_id   )values
			(111,  '%s', '2023-01-01', '좋아요 좋아요 ', 'N', NULL, 111,112),
			(113,  '2023-01-03', '2023-01-01', '으웩 으웩', 'N',111 , 111, 112)
			;
			""", LocalDateTime.now());
		PreparedStatement statement = connection.prepareStatement(reviewCommentSql);
		statement.executeUpdate();
	}

	static void prepareReviewData(final Connection connection) throws SQLException {
		final String reviewSql = String.format("""
				insert into review (id, created_at, modified_at, content, is_block, is_delete, is_hide, is_new, bakery_id, user_id   )values
				 (111,  '%s',         '2023-01-01', '좋아요!',  'N', 'N', 'N','N',111, 111),
				 (112,  '2023-01-02', '2023-01-01', '맛있어요!', 'N', 'N', 'N','N',111,113),
				 (113,  '2023-01-03', '2023-01-01', '으웩!',    'Y', 'N','N','N',112, 112)
			 ;
			""", LocalDateTime.now());
		PreparedStatement statement = connection.prepareStatement(reviewSql);
		statement.executeUpdate();
	}

	static void preparePostData(final Connection connection) throws SQLException {
		final String reviewSql = String.format("""
			insert into post(id ,created_at ,modified_at ,content ,is_block ,is_delete ,is_hide ,post_topic ,title ,user_id) values
			(999, '2023-01-01','2023-01-01','test 222 content',false,false,false,'BREAD_STORY','test title', 111)
			 ;
			""", LocalDateTime.now());
		PreparedStatement statement = connection.prepareStatement(reviewSql);
		statement.executeUpdate();
	}
}
