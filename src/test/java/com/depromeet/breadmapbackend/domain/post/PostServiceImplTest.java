package com.depromeet.breadmapbackend.domain.post;

import static com.depromeet.breadmapbackend.domain.post.PostTestData.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.post.comment.Comment;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLike;
import com.depromeet.breadmapbackend.domain.post.dto.EventCarouselInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;
import com.depromeet.breadmapbackend.domain.post.like.PostLike;
import com.depromeet.breadmapbackend.global.dto.PageCommunityResponseDto;

/**
 * PostServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
class PostServiceImplTest extends PostServiceTest {

	@Autowired
	private PostServiceImpl sut;

	@Autowired
	private EntityManager em;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-test-data.sql"));
			prepareReviewData(connection);
			prepareReviewCommentData(connection);
			preparePostLikeData(connection);
		}
	}

	@Test
	void 빵이야기_등록() throws Exception {
		//given
		final Long userId = 111L;
		final String title = "title test";
		final String content = "content test";
		final PostTopic topic = PostTopic.BREAD_STORY;
		final List<String> images = List.of("image1", "image2");
		final PostRegisterCommand command = new PostRegisterCommand(title, content, images, topic);

		//when
		sut.register(command, userId);
		//then
		final Post savedPost = em.createQuery("select p from Post p where p.user.id =:userId ", Post.class)
			.setParameter("userId", userId)
			.getSingleResult();

		assertThat(savedPost.getTitle()).isEqualTo(title);
		assertThat(savedPost.getContent()).isEqualTo(content);
		assertThat(savedPost.getPostTopic()).isEqualTo(topic);
		assertThat(savedPost.getImages()).hasSize(2);
		assertThat(savedPost.getImages().stream().map(PostImage::getImage).toList())
			.containsExactly("image1", "image2");
		assertThat(savedPost.getUser().getNickName()).isEqualTo("nick_name");
	}

	@Test
	void 빵이야기_상세_조회() throws Exception {
		//given
		final Long userId111 = 111L;
		final Long userId112 = 112L;
		final Long postId222 = 222L;
		final Long postId223 = 223L;

		//when
		final PostDetailInfo result = sut.getDetailPost(postId222, userId111, PostTopic.BREAD_STORY);

		//then
		assertThat(result.followerCount()).isEqualTo(2);
		assertThat(result.reviewCount()).isEqualTo(1);
		assertThat(result.isFollowed()).isTrue();
		assertThat(result.likeCount()).isEqualTo(3);
		assertThat(result.commentCount()).isEqualTo(3);
		assertThat(result.title()).isEqualTo("test title");
		assertThat(result.content()).isEqualTo("test 222 content");
		assertThat(result.createdDate()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
		assertThat(result.images()).hasSize(2);
		assertThat(result.isUserCommented()).isTrue();
		assertThat(result.isUserLiked()).isTrue();

		//when
		final PostDetailInfo result2 = sut.getDetailPost(postId223, userId112, PostTopic.BREAD_STORY);
		//then
		assertThat(result2.isUserCommented()).isFalse();
		assertThat(result2.isUserLiked()).isFalse();
	}

	@Test
	void 커뮤니티_전체_조회() throws Exception {
		//given
		final Long userId = 111L;
		final CommunityPage page = new CommunityPage(0L, 0L, 0);
		//when
		final PageCommunityResponseDto<CommunityCardResponse> result = sut.getCommunityCards(page, userId,
			PostTopic.ALL);
		//then
		assertThat(result.getContents().size()).isEqualTo(4);
		assertThat(result.getContents().get(0).writerInfo().userId()).isEqualTo(112L);
		assertThat(result.getContents().get(0).postId()).isEqualTo(224L);
		assertThat(result.getContents().get(0).postTopic().name()).isEqualTo("EVENT");
		assertThat(result.getContents().get(1).postTopic().name()).isEqualTo("REVIEW");
		assertThat(result.getContents().get(3).isUserCommented()).isTrue();
		assertThat(result.getContents().get(3).isUserLiked()).isTrue();

		//given
		final Long secondUserId = 113L;
		final CommunityPage pageOne = new CommunityPage(1L, 1L, 1);
		//when
		final PageCommunityResponseDto<CommunityCardResponse> secondResult = sut.getCommunityCards(pageOne,
			secondUserId,
			PostTopic.ALL);
		//then
		assertThat(secondResult.getContents().size()).isEqualTo(5);
		assertThat(secondResult.getContents().get(0).writerInfo().userId()).isEqualTo(113L);
		assertThat(secondResult.getContents().get(0).postId()).isEqualTo(225L);
		assertThat(secondResult.getContents().get(0).postTopic().name()).isEqualTo("EVENT");
		assertThat(secondResult.getContents().get(1).postTopic().name()).isEqualTo("REVIEW");
		assertThat(secondResult.getContents().get(1).bakeryInfo().bakeryId()).isEqualTo(112L);
		assertThat(secondResult.getContents().get(2).postTopic().name()).isEqualTo("BREAD_STORY");
		assertThat(secondResult.getContents().get(2).bakeryInfo().bakeryId()).isEqualTo(0L);
	}

	@Test
	void 커뮤니티_빵이야기_조회() throws Exception {
		//given
		final Long userId = 113L;
		final CommunityPage page = new CommunityPage(0L, 0L, 0);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> result = sut.getCommunityCards(page, userId,
			PostTopic.BREAD_STORY);

		//then
		assertThat(result.getContents().size()).isEqualTo(3);
		assertThat(result.getContents().get(0).writerInfo().userId()).isEqualTo(112L);
		assertThat(result.getContents().get(0).postId()).isEqualTo(224L);
		assertThat(result.getContents().get(0).postTopic().name()).isEqualTo("EVENT");
		assertThat(result.getContents().get(1).postTopic().name()).isEqualTo("BREAD_STORY");
		assertThat(result.getContents().get(2).postTopic().name()).isEqualTo("BREAD_STORY");

		//given
		final CommunityPage pageOne = new CommunityPage(0L, 2L, 1);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> secondResult = sut.getCommunityCards(pageOne, userId,
			PostTopic.BREAD_STORY);

		//then
		assertThat(secondResult.getContents().size()).isEqualTo(1);
		assertThat(secondResult.getContents().get(0).postId()).isEqualTo(222L);
	}

	@Test
	void 커뮤니티_이벤트_조회() throws Exception {
		//given
		final Long userId = 113L;
		final CommunityPage page = new CommunityPage(0L, 0L, 0);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> result = sut.getCommunityCards(page, userId,
			PostTopic.EVENT);

		//then
		assertThat(result.getContents().size()).isEqualTo(2);
		assertThat(result.getContents().get(0).writerInfo().userId()).isEqualTo(112L);
		assertThat(result.getContents().get(0).postId()).isEqualTo(224L);
		assertThat(result.getContents().get(0).postTopic().name()).isEqualTo("EVENT");
		assertThat(result.getContents().get(1).postTopic().name()).isEqualTo("EVENT");

		//given
		final CommunityPage pageOne = new CommunityPage(0L, 2L, 1);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> secondResult = sut.getCommunityCards(pageOne, userId,
			PostTopic.EVENT);

		//then
		assertThat(secondResult.getContents().size()).isEqualTo(0);
	}

	@Test
	void 커뮤니티_리뷰_조회() throws Exception {
		//given
		final Long userId = 113L;
		final CommunityPage page = new CommunityPage(0L, 0L, 0);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> result = sut.getCommunityCards(page, userId,
			PostTopic.REVIEW);

		//then
		assertThat(result.getContents().size()).isEqualTo(4);
		assertThat(result.getContents().get(0).writerInfo().userId()).isEqualTo(112L);
		assertThat(result.getContents().get(0).postId()).isEqualTo(224L);
		assertThat(result.getContents().get(0).postTopic().name()).isEqualTo("EVENT");
		assertThat(result.getContents().get(1).postTopic().name()).isEqualTo("REVIEW");

		//given
		final CommunityPage pageOne = new CommunityPage(2L, 0L, 1);

		//when
		final PageCommunityResponseDto<CommunityCardResponse> secondResult
			= sut.getCommunityCards(pageOne, userId, PostTopic.REVIEW);

		//then
		assertThat(secondResult.getContents().size()).isEqualTo(2);
		assertThat(secondResult.getContents().get(0).postId()).isEqualTo(113L);
	}

	@Test
	void 커뮤니티_추천글_조회() throws Exception {
		//given
		final Long userId = 111L;
		//block user 113L
		// post rank 222( like 2, userId 112) 223 (like 1, userId 113)
		// review rank 111 ( date 1st, comment 1 ) 112, 113,
		//when
		final List<CommunityCardResponse> hotPosts = sut.findHotPosts(userId);

		//then
		assertThat(hotPosts).hasSize(3);
		assertThat(hotPosts.get(0).postTopic()).isEqualTo(PostTopic.EVENT);
		assertThat(hotPosts.get(1).postTopic()).isEqualTo(PostTopic.REVIEW);
		assertThat(hotPosts.get(1).postId()).isEqualTo(111L);

		assertThat(hotPosts.get(2).postTopic()).isEqualTo(PostTopic.BREAD_STORY);
		assertThat(hotPosts.get(2).postId()).isEqualTo(222L);
	}

	@Test
	void 좋아요() throws Exception {
		//given
		final Long userId = 111L;
		final Long postId = 225L;

		//when
		final int reuslt = sut.toggle(postId, userId);

		//then
		assertThat(reuslt).isEqualTo(1);
		final PostLike postLike = em.createQuery(
				"select pl from PostLike pl where pl.userId =:userId and pl.post.id =:postId", PostLike.class)
			.setParameter("userId", userId)
			.setParameter("postId", postId)
			.getSingleResult();

		assertThat(postLike.getUserId()).isEqualTo(userId);
		assertThat(postLike.getPost().getId()).isEqualTo(postId);

		//when
		final int reuslt2 = sut.toggle(postId, userId);

		//then
		assertThat(reuslt2).isEqualTo(0);
		assertThatThrownBy(() -> em.createQuery(
				"select pl from PostLike pl where pl.userId =:userId and pl.post.id =:postId", PostLike.class)
			.setParameter("userId", userId)
			.setParameter("postId", postId)
			.getSingleResult()
		).isInstanceOf(NoResultException.class);
	}

	@Test
	void 포스트_수정() throws Exception {
		//given
		final Long userId = 112L;
		final Long postId = 222L;
		final String title = "updated title";
		final String content = "updated content";
		final PostTopic postTopic = PostTopic.BREAD_STORY;
		final List<String> images = List.of("updated image1", "updated image2");
		final PostUpdateCommand command = new PostUpdateCommand(
			postId,
			title,
			content,
			postTopic,
			images
		);

		//when
		sut.update(userId, command);

		//then
		final Post updatedPost = em.find(Post.class, postId);
		assertThat(updatedPost.getTitle()).isEqualTo(title);
		assertThat(updatedPost.getContent()).isEqualTo(content);
		assertThat(updatedPost.getPostTopic()).isEqualTo(postTopic);
		assertThat(updatedPost.getImages().stream().map(PostImage::getImage).toList()).isEqualTo(images);
	}

	@Test
	void 커뮤니티_삭제() throws Exception {
		//given
		final Long userId = 112L;
		final Long postId = 222L;

		// when
		sut.delete(postId, PostTopic.BREAD_STORY, userId);

		//then
		final List<Post> postResult = em.createQuery("select p from Post p where p.id =:postId", Post.class)
			.setParameter("postId", postId)
			.getResultList();
		final List<PostLike> postLikeResult = em.createQuery("select pl from PostLike pl where pl.post.id =:postId",
				PostLike.class)
			.setParameter("postId", postId)
			.getResultList();
		final List<Comment> commentResult = em.createQuery("select c from Comment c where c.postId =:postId",
				Comment.class)
			.setParameter("postId", postId)
			.getResultList();
		final List<CommentLike> commentLikeResult = em.createQuery(
				"select c from CommentLike c where c.comment.id in (:postId)",
				CommentLike.class)
			.setParameter("postId", List.of(111L, 112L, 113L))
			.getResultList();

		assertThat(postResult).isEmpty();
		assertThat(postLikeResult).isEmpty();
		assertThat(commentResult).isEmpty();
		assertThat(commentLikeResult).isEmpty();
	}

	@Test
	void 이벤트_캐러셀_조회() throws Exception {
		//given
		//when
		final List<EventCarouselInfo> results = sut.getEventCarousels();
		//then
		assertThat(results).hasSize(1);

	}

}