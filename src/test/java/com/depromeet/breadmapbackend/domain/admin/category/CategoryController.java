package com.depromeet.breadmapbackend.domain.admin.category;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryAssembler;
import com.depromeet.breadmapbackend.domain.admin.category.dto.CategoryResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

public class CategoryController extends ControllerTest {

	private Admin admin;
	private JwtToken token;

	@BeforeEach
	public void setup() throws IOException {
		admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
		adminRepository.save(admin);
		token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());
	}

	@AfterEach
	public void setDown() {
		adminRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("모든 카테고리를 조회한다")
	void 모든_카테고리를_조회() throws Exception {

		//given
		List<Category> categories = List.of(
			new Category("추천 빵집"),
			new Category("월별 트렌드 빵집")
		);

		List<Category> categoryList = categoryRepository.saveAll(categories);

		List<CategoryResponse> content = CategoryAssembler.toDto(categoryList);

		ApiResponse<List<CategoryResponse>> res = new ApiResponse<>(content);

		String response = objectMapper.writeValueAsString(res);

		//when
		ResultActions perform = mockMvc.perform(get("/v1/admin/category/all")
			.header("Authorization", "Bearer " + token.getAccessToken())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE));

		perform.andExpect(status().isOk())
			.andExpect(content().string(response));

		//then
		perform.andDo(print()).
			andDo(document("find-all-category",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(headerWithName("Authorization").description("어드민 유저의 Access Token")),
					responseFields(
						fieldWithPath("data.[].categoryId").description("카테고리 아이디"),
						fieldWithPath("data.[].categoryName").description("카테고리 이름")
					)
				)
			);
	}
}
