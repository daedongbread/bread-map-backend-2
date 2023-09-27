package com.depromeet.breadmapbackend.domain.admin.carousel.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.ResultActions;

/**
 * PostAdminControllerAssertions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public class CarouselManagerAssertions {

    public static void 이벤트_순서_수정_결과_검증(final ResultActions 결과) throws Exception {
        결과.andExpect(status().isOk());
    }

    public static void 이벤트_캐러셀_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
        결과.andExpect(status().isOk());
    }

}
