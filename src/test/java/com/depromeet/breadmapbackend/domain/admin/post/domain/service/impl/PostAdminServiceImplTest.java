package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselType;
import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;
import com.depromeet.breadmapbackend.domain.post.Post;
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
        final Page<PostManagerMapperInfo> result = sut.getEventPosts(0);
        //then
        final List<PostManagerMapperInfo> content = result.getContent();
        assertThat(content).hasSize(10);
        final List<Long> resultIdList = content.stream().map(PostManagerMapperInfo::managerId).toList();
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
        final EventCommand command =
            new EventCommand(
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

        final CarouselManager carouselManager = em.createQuery(
                "select c from CarouselManager c where c.targetId =:id and c.carouselType = :carouselType ",
                CarouselManager.class)
            .setParameter("id", result.getId())
            .setParameter("carouselType", CarouselType.EVENT)
            .getSingleResult();
        assertThat(savedPostManagerMapper.isFixed()).isTrue();
        assertThat(carouselManager.getCarouselOrder()).isEqualTo(1);

        final Long fixedCount = em.createQuery(
                "select count(pmm) from PostManagerMapper pmm where pmm.isFixed = true", Long.class)
            .getSingleResult();
        assertThat(fixedCount).isEqualTo(1L);

    }

    @Test
    void 이벤트_수정() throws Exception {
        //given
        final Long managerId = 121L;
        final String title = "title test";
        final String content = "content test";
        final String bannerImage = "banner image Test";
        final List<String> images = List.of("image 1", "images 2");
        final EventCommand command =
            new EventCommand(
                true,
                true,
                false,
                title,
                content,
                bannerImage,
                images
            );
        //when
        sut.updateEventPost(command, managerId);

        //then
        final PostManagerMapper savedPostManagerMapper = em.createQuery(
                "select pmm from PostManagerMapper pmm join fetch pmm.post p where pmm.id =:id",
                PostManagerMapper.class)
            .setParameter("id", managerId)
            .getSingleResult();

        final CarouselManager carouselManager = em.createQuery(
                "select c from CarouselManager c where c.targetId =:id and c.carouselType = :carouselType ",
                CarouselManager.class)
            .setParameter("carouselType", CarouselType.EVENT)
            .setParameter("id", savedPostManagerMapper.getPost().getId())
            .getSingleResult();

        assertThat(savedPostManagerMapper.isFixed()).isTrue();
        assertThat(carouselManager.getCarouselOrder()).isEqualTo(0);
        assertThat(carouselManager.getBannerImage()).isEqualTo(bannerImage);

        final Post updatedPostEvent = savedPostManagerMapper.getPost();
        assertThat(updatedPostEvent.getContent()).isEqualTo(content);
        assertThat(updatedPostEvent.getTitle()).isEqualTo(title);

        final Long fixedCount = em.createQuery(
                "select count(pmm) from PostManagerMapper pmm where pmm.isFixed = true", Long.class)
            .getSingleResult();
        assertThat(fixedCount).isEqualTo(1L);
        //
        // final List<PostManagerMapper> resultList = em.createQuery(
        // 		"select pmm from PostManagerMapper pmm where isCarousel is true order by carouselOrder",
        // 		PostManagerMapper.class)
        // 	.getResultList();
        // assertThat(resultList.stream().map(PostManagerMapper::getId))
        // 	.containsExactly(113L, 112L, 127L, 114L, 115L, 116L, 117L, 118L, 119L, 120L, 123L, 122L, 124L, 125L, 126L);
        // assertThat(resultList.get(resultList.size() - 1).getCarouselOrder()).isEqualTo(15);
    }
    //
    // @Test
    // void 캐러셀_순서_수정() throws Exception {
    // 	//given
    // 	final List<UpdateCarouselOrderCommand> command = List.of(
    // 		new UpdateCarouselOrderCommand(112L, 16),
    // 		new UpdateCarouselOrderCommand(126L, 1),
    // 		new UpdateCarouselOrderCommand(113L, 2)
    // 	);
    //
    // 	//when
    // 	sut.updateEventOrder(command);
    //
    // 	//then
    // 	final List<PostManagerMapper> resultList = em.createQuery(
    // 			"select pmm from PostManagerMapper pmm where pmm.id in (:ids) order by carouselOrder",
    // 			PostManagerMapper.class)
    // 		.setParameter("ids", List.of(112L, 113L, 126L))
    // 		.getResultList();
    //
    // 	assertThat(resultList.get(0).getCarouselOrder()).isEqualTo(1);
    // 	assertThat(resultList.get(0).getId()).isEqualTo(126L);
    // 	assertThat(resultList.get(1).getCarouselOrder()).isEqualTo(2);
    // 	assertThat(resultList.get(1).getId()).isEqualTo(113L);
    // 	assertThat(resultList.get(2).getCarouselOrder()).isEqualTo(16);
    // 	assertThat(resultList.get(2).getId()).isEqualTo(112L);
    // }
    //
    // @Test
    // void 캐러셀_조회() throws Exception {
    // 	//given
    // 	//when
    // 	final List<CarouselInfo> result = sut.getCarousels();
    // 	//then
    // 	assertThat(result).hasSize(16);
    // 	final CarouselInfo firstEvent = result.get(0);
    // 	assertThat(firstEvent.order()).isEqualTo(1);
    // 	assertThat(firstEvent.bannerImage()).isEqualTo("bannerImage26");
    // 	assertThat(firstEvent.managerId()).isEqualTo(113L);
    // 	assertThat(firstEvent.title()).isEqualTo("test title3 event");
    //
    // }
}