package com.depromeet.breadmapbackend.web.advice;

import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.web.controller.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 잘못된 URL 접근 에러
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse noHandlerFoundException(NoHandlerFoundException e) {
        return new ErrorResponse(
                "Wrong URL access.\n" +
                "Not Found URL : " + e.getRequestURL()
        );
    }

    /**
     * refresh 갱신할 때, refresh 토큰 유효 X
     */
    @ExceptionHandler(TokenValidFailedException.class)
    public ErrorResponse tokenValidFailedExHandler(TokenValidFailedException e) {
        return new ErrorResponse(e.getMessage());
    }

}
