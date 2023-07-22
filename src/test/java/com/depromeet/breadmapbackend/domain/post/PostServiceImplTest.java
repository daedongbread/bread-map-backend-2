package com.depromeet.breadmapbackend.domain.post;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.image.PostImage;
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

	@Test
	@Sql("classpath:post-test-data.sql")
	void 빵이야기_등록() throws Exception {
		//given
		final Long userId = 111L;
		final String title = "title test";
		final String content = "content test";
		final PostTopic topic = PostTopic.BREAD_STORY;
		final List<String> images = List.of("image1", "image2");
		final PostRegisterCommand command = new PostRegisterCommand(title, content, images);

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
	@Sql("classpath:post-test-data.sql")
	void 빵이야기_상세_조회() throws Exception {
		//given
		final Long userId = 111L;
		final Long postId = 222L;

		//when
		final PostDetailInfo result = sut.getPost(postId, userId, PostTopic.BREAD_STORY);

		//then
		assertThat(result.followerCount()).isEqualTo(2);
		assertThat(result.reviewCount()).isEqualTo(1);
		assertThat(result.isFollowed()).isTrue();
		assertThat(result.likeCount()).isEqualTo(2);
		assertThat(result.commentCount()).isEqualTo(3);
		assertThat(result.title()).isEqualTo("test title");
		assertThat(result.content()).isEqualTo("test 222 content");
		assertThat(result.createdDate()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
		assertThat(result.images()).hasSize(2);

	}

	@Test
	@Sql("classpath:post-test-data.sql")
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
		assertThat(secondResult.getContents().get(1).postTopic().name()).isEqualTo("BREAD_STORY");
		assertThat(secondResult.getContents().get(1).bakeryInfo().bakeryId()).isEqualTo(0L);
		assertThat(secondResult.getContents().get(2).postTopic().name()).isEqualTo("REVIEW");
		assertThat(secondResult.getContents().get(2).bakeryInfo().bakeryId()).isEqualTo(111L);
	}

	@Test
	@Sql("classpath:post-test-data.sql")
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
	@Sql("classpath:post-test-data.sql")
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
	@Sql("classpath:post-test-data.sql")
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
		assertThat(secondResult.getContents().get(0).postId()).isEqualTo(112L);
	}

	@Test
	@Sql("classpath:post-test-data.sql")
	void 커뮤니티_추천글_조회() throws Exception {
		//given
		final Long userId = 111L;

		//when
		final List<CommunityCardResponse> hotPosts = sut.findHotPosts(userId);
		final CommunityCardResponse communityCardResponse = hotPosts.get(0);

		//then
	}
}