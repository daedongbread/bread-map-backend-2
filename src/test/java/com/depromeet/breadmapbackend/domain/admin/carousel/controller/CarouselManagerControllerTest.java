package com.depromeet.breadmapbackend.domain.admin.carousel.controller;

import static com.depromeet.breadmapbackend.domain.admin.carousel.controller.CarouselManagerAssertions.이벤트_순서_수정_결과_검증;
import static com.depromeet.breadmapbackend.domain.admin.carousel.controller.CarouselManagerControllerTestSteps.이벤트_순서_수정_요청;
import static com.depromeet.breadmapbackend.domain.admin.carousel.controller.CarouselManagerControllerTestSteps.이벤트_캐러셀_조회_요청;
import static com.depromeet.breadmapbackend.domain.admin.post.controller.PostAdminControllerAssertions.이벤트_캐러셀_조회_결과_검증;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.UpdateCarouselOrderCommand;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * CarouselManagerControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/09/28
 */
class CarouselManagerControllerTest extends ControllerTest {

    private String 관리자_토큰;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        setUpTestDate();
        관리자_토큰 = jwtTokenProvider.createJwtToken("admin@email.com", RoleType.ADMIN.getCode()).getAccessToken();
    }

    @Test
    void 이벤트_정렬_순서_수정() throws Exception {
        // given
        final var 이벤트_순서_수정_요청_데이터 = List.of(
            new UpdateCarouselOrderCommand(112L, 16),
            new UpdateCarouselOrderCommand(126L, 1),
            new UpdateCarouselOrderCommand(113L, 2)
        );

        // when
        final var 결과 = 이벤트_순서_수정_요청(이벤트_순서_수정_요청_데이터, 관리자_토큰, mockMvc, objectMapper);
        // then
        이벤트_순서_수정_결과_검증(결과);
    }


    @Test
    void 이벤트_캐러셀_조회() throws Exception {
        // given
        // when
        final var 결과 = 이벤트_캐러셀_조회_요청(관리자_토큰, mockMvc);
        // then
        이벤트_캐러셀_조회_결과_검증(결과);
    }

    private void setUpTestDate() throws Exception {
        try (final Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-admin-test-data.sql"));
        }
    }
}