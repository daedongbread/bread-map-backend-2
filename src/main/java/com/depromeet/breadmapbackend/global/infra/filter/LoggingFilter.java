package com.depromeet.breadmapbackend.global.infra.filter;

import static com.depromeet.breadmapbackend.global.infra.filter.LoggerTrxKey.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.depromeet.breadmapbackend.global.security.filter.BufferedRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		if (!request.getRequestURI().equals("/v1/actuator/health")) {
			final String trxId = UUID.randomUUID().toString().substring(0, 8);
			MDC.put(TRX_ID.name(), trxId);
			MDC.put(TRX_TIME.name(), String.valueOf(System.currentTimeMillis()));
			request = requestLog(request);
		}
		filterChain.doFilter(request, response);

		if (!request.getRequestURI().equals("/v1/actuator/health")) {
			log.info("Request Ended ==>  [REQUEST_TIME] : {} ms",
				MDC.get(TRX_TIME.name()) != null ?
					(System.currentTimeMillis() - Long.parseLong(MDC.get(TRX_TIME.name()))) : 0);
		}

		MDC.clear();
	}

	private HttpServletRequest requestLog(HttpServletRequest request) throws IOException {
		// [Method] : endpoint
		log.info("Request Started ==> [ {} ] : {}",
			request.getMethod(),
			request.getRequestURI()

		);
		// query :
		if (request.getQueryString() != null)
			log.info("query : " + request.getQueryString());

		String contentType = request.getContentType();
		boolean isMultipartRequest = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartRequest && contentType != null && !contentType.toLowerCase()
			.startsWith("multipart/form-data")) {
			String requestBody = readBody(request);
			if (!requestBody.isEmpty()) {
				// request body :
				log.info("request body : ");
				log.info(requestBody);
				request = new BufferedRequestWrapper(request, requestBody);
			}
		}
		return request;
	}

	private String readBody(HttpServletRequest request) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try (InputStream inputStream = request.getInputStream()) {
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
		return stringBuilder.toString();
	}
}
