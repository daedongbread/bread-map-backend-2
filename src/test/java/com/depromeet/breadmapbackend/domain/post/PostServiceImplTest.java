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
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

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
	@Sql("classpath:post-test-data.sql")
	void 빵이야기_상세_조회() throws Exception {
		//given
		final Long userId = 111L;
		final Long postId = 222L;

		//when
		final PostDetailInfo result = sut.getPost(postId, userId);

		//then
		assertThat(result.followerCount()).isEqualTo(2);
		assertThat(result.reviewCount()).isEqualTo(2);
		assertThat(result.isFollowed()).isTrue();
		assertThat(result.likeCount()).isEqualTo(2);
		assertThat(result.commentCount()).isEqualTo(3);
		assertThat(result.post().getTitle()).isEqualTo("test title");
		assertThat(result.post().getContent()).isEqualTo("test 222 content");
		assertThat(result.post().getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0));
		assertThat(result.post().getImages()).hasSize(2);

	}

	@Test
	@Sql("classpath:post-test-data.sql")
	void 커뮤니티_전체_조회() throws Exception {
		//given
		final CommunityPage page = new CommunityPage(1L, 1L, 1);
		//when
		final PageResponseDto<CommunityCardResponse> result = sut.getAllPosts(page);
		//then
		assertThat(result.getContents().size()).isEqualTo(6);
		assertThat(result.getContents().get(0).writerInfo().userId()).isEqualTo(112L);

	}
}