package com.depromeet.breadmapbackend.domain.admin.bakery;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddDto;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddRequest;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * AdminBakeryAcceptanceTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/17
 */
public class AdminBakeryAcceptanceTest extends ControllerTest {

	private String 사용자_토큰;

	@Autowired
	private DataSource dataSource;

	@Autowired
	EntityManager em;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("admin@email.com", RoleType.ADMIN.getCode()).getAccessToken();
	}

	@Test
	void 빵집_제보id_없이_빵집_등록을_요청하면_기대하는_응답을_반환한다() throws Exception {
		//given
		final BakeryAddRequest request = FixtureFactory.getBakeryAddDto(null)
			.nextObject(BakeryAddRequest.class);
		//when
		//then
		mockMvc.perform(
				post("/v1/admin/bakeries")
					.header("Authorization", "Bearer " + 사용자_토큰)
					.content(objectMapper.writeValueAsString(request))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void 빵집_등록을_요청하면_기대하는_응답을_반환한다() throws Exception {
		//given
		final BakeryAddRequest request = FixtureFactory.getBakeryAddDto(111L)
			.nextObject(BakeryAddRequest.class);

		//when
		//then
		final String response = mockMvc.perform(
				post("/v1/admin/bakeries")
					.header("Authorization", "Bearer " + 사용자_토큰)
					.content(objectMapper.writeValueAsString(request))
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		final Response date = objectMapper.readValue(response, Response.class);
		final Bakery savedBakery = em.createQuery("select b from Bakery b where b.id = :id", Bakery.class)
			.setParameter("id", date.data().getBakeryId())
			.getSingleResult();
		assertThat(savedBakery.getPioneer().getId()).isEqualTo(112L);
		assertThat(savedBakery.getBakeryAddReport().getId()).isEqualTo(111L);
	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("bakery-Admin-test-data.sql"));
		}
	}

	record Response(BakeryAddDto data) {

	}
}
