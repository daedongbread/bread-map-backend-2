package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.CreateEventCommand;

/**
 * PostAdminServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
class PostAdminServiceImplTest extends PostAdminServiceTest {

	@Autowired
	private PostAdminServiceImpl sut;

	@Autowired
	private EntityManager em;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-admin-test-data.sql"));

		}
	}

	@Test
	void 커뮤니티_관리자_페이지_조회() throws Exception {
		//given
		//when
		final Page<PostManagerMapper> result = sut.getPosts(0);
		//then
		final List<PostManagerMapper> content = result.getContent();
		assertThat(content).hasSize(10);
		final List<Long> resultIdList = content.stream().map(PostManagerMapper::getId).toList();
		final List<Long> resultPostIdList = content.stream().map(mapper -> mapper.getPost().getId()).toList();
		assertThat(resultPostIdList).containsExactly(229L, 230L, 231L, 232L, 233L, 234L, 235L, 236L, 237L, 238L);
		assertThat(resultIdList).containsExactly(116L, 117L, 118L, 119L, 120L, 121L, 122L, 123L, 124L, 125L);
	}

	@Test
	void 이벤트_고정_가능_여부() throws Exception {
		//give
		//when
		final boolean result = sut.canFixEvent();
		//then
		assertThat(result).isFalse();
	}

	@Test
	void 이벤트_등록() throws Exception {
		//given
		final String title = "title test";
		final String content = "content test";
		final String bannerImage = "banner image Test";
		final List<String> images = List.of("image 1", "images 2");
		final CreateEventCommand command =
			new CreateEventCommand(
				true,
				true,
				true,
				title,
				content,
				bannerImage,
				images
			);
		//when
		final PostManagerMapper result = sut.createEventPost(command);
		//then
		assertThat(result.getId()).isNotNull();
		final PostManagerMapper savedPostManagerMapper = em.createQuery(
				"select pmm from PostManagerMapper pmm where pmm.id =:id",
				PostManagerMapper.class)
			.setParameter("id", result.getId())
			.getSingleResult();

		assertThat(savedPostManagerMapper.isFixed()).isTrue();
		final Long fixedCount = em.createQuery(
				"select count(pmm) from PostManagerMapper pmm where pmm.isFixed = true", Long.class)
			.getSingleResult();
		assertThat(fixedCount).isEqualTo(1L);

	}
}