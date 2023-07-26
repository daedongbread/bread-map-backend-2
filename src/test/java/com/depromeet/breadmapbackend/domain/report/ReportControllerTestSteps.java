package com.depromeet.breadmapbackend.domain.report;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.report.dto.request.ReportRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PostControllerTestSteps
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class ReportControllerTestSteps {

	public static ResultActions 커뮤니티_신고_요청(
		final ReportRequest 신고_요청_데이터,
		final String 신고_대상_커뮤니티_타입,
		final int 신고_대상_커뮤니티_고유_번호,
		final String 사용자_토큰,
		final MockMvc mockMvc,
		final ObjectMapper objectMapper
	) throws Exception {
		final String request = objectMapper.writeValueAsString(신고_요청_데이터);
		return mockMvc.perform(post("/v1/reports/{reportType}/{contentId}", 신고_대상_커뮤니티_타입, 신고_대상_커뮤니티_고유_번호)
				.content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/reports/add",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("reportType").description("신고 대상 커뮤니티 타입 \n"
						+ "irrelevant_content 관계없는 내용\n"
						+ "inappropriate_content 음란성, 욕설 등 부적절한 내용\n"
						+ "irrelevant_image 관련없는 사진 게시\n"
						+ "unfit_content 작성 취지에 맞지 않는 내용(복사글 등)\n"
						+ "copyright_theft 저작권 도용 의심(사진 등)\n"
						+ "etc 기타(하단 내용 작성) "),
					parameterWithName("contentId").description("신고 대상 커뮤니티 고유 번호")
				),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("reason").description("신고 사유  \n"
						+ "IRRELEVANT_CONTENT 관계없는 내용\n"
						+ "INAPPROPRIATE_CONTENT 음란성, 욕설 등 부적절한 내용\n"
						+ "IRRELEVANT_IMAGE 관련없는 사진 게시\n"
						+ "UNFIT_CONTENT 작성 취지에 맞지 않는 내용(복사글 등)\n"
						+ "COPYRIGHT_THEFT 저작권 도용 의심(사진 등)\n"
						+ "ETC 기타(하단 내용 작성) "),
					fieldWithPath("content").description("신고 기타 내용")
				)
			));
	}

}
