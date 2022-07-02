package com.depromeet.breadmapbackend.web.advice;

import com.depromeet.breadmapbackend.domain.bakery.exception.*;
import com.depromeet.breadmapbackend.domain.flag.exception.*;
import com.depromeet.breadmapbackend.domain.review.exception.*;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.security.CAccessDeniedException;
import com.depromeet.breadmapbackend.security.CAuthenticationEntryPointException;
import com.depromeet.breadmapbackend.security.exception.RefreshTokenNotFoundException;
import com.depromeet.breadmapbackend.security.exception.TokenValidFailedException;
import com.depromeet.breadmapbackend.web.controller.common.ErrorResponse;
import com.depromeet.breadmapbackend.web.controller.review.DataNotExistedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.ParseException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    /*
     * default Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse defaultException(HttpServletRequest request, Exception e) {
        log.info(String.valueOf(e));
        e.printStackTrace();
        return new ErrorResponse("Unknown error");
    }

    /*
     * Request Body validation Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse requestBodyNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            log.error("error field : \"{}\", value : \"{}\", message : \"{}\"", error.getField(), error.getRejectedValue(), error.getDefaultMessage());
        }
        return new ErrorResponse("Request body's field is not valid");
    }

    /*
     * Request Body validation Exception
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse requestBodyNotValidException(HttpServletRequest request, WebExchangeBindException e) {
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            log.error("error field : \"{}\", value : \"{}\", message : \"{}\"", error.getField(), error.getRejectedValue(), error.getDefaultMessage());
        }
        return new ErrorResponse("Request part's field is not valid");
    }

    /*
     * Request Body wrong Exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse requestBodyWrongException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.info(e.getMessage());
        return new ErrorResponse("Request Body is wrong");
    }

    /*
     * Parse Exception
     */
    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse parseException(HttpServletRequest request, ParseException e) {
        return new ErrorResponse("Parse Exception");
    }

    /*
     * Path Variable, Query Parameter validation Exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse argumentNotValidException(HttpServletRequest request, ConstraintViolationException e) {
        for (ConstraintViolation<?> error : e.getConstraintViolations()) {
            log.error("error field : \"{}\", value : \"{}\", message : \"{}\"",
                    error.getPropertyPath().toString().split(".")[1], error.getInvalidValue(), error.getMessage());
        }
        return new ErrorResponse("Path Variable or Query Parameter Not Valid");
    }

    /*
     * Path Variable missing Exception
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableMissingException(HttpServletRequest request, MissingPathVariableException e) {
        log.error("error field : \"{}\", message : \"{}\"", e.getVariableName(), e.getMessage());
        return new ErrorResponse("Path Variable is missing");
    }

    /*
     * Path Variable Type Mismatch Exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.error("error field : \"{}\", value : \"{}\", message : \"{}\"", e.getName(), e.getValue(), e.getMessage());
        return new ErrorResponse("Path Variable Type is mismatch");
    }

    /*
     * Query Parameter missing Exception
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableMissingException(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error("error field : \"{}\", message : \"{}\"", e.getParameterName(), e.getMessage());
        return new ErrorResponse("Query Parameter is missing");
    }

    /*
     * 틀린 URL 로 접근했을 경우 발생 시키는 예외
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse wrongURLException(HttpServletRequest request, NoHandlerFoundException e) {
        return new ErrorResponse("Wrong URL");
    }

    /*
     * 틀린 접근방식 (GET, POST, ..) 으로 접근했을 경우 발생 시키는 예외
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ErrorResponse wrongMethodException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse("Wrong Method");
    }

    /*
     * 전달한 Jwt 이 정상적이지 않은 경우 발생 시키는 예외
     */
    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //
    protected ErrorResponse authenticationEntrypointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        return new ErrorResponse("Invalid JWT");
    }

    /*
     * 권한이 없는 리소스를 요청한 경우 발생 시키는 예외
     */
    @ExceptionHandler(CAccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse accessDeniedException(HttpServletRequest request, CAccessDeniedException e) {
        return new ErrorResponse("Access Denied");
    }


    /**
     * refresh 갱신할 때, refresh 토큰 유효 X
     */
    @ExceptionHandler(TokenValidFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse tokenValidFailedExHandler(TokenValidFailedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * refresh 토큰이 존재하지 않을 때
     */
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse refreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 가져올 데이터가 존재하지 않을 때
     */
    @ExceptionHandler(DataNotExistedException.class)
    public ErrorResponse DataNotExistedException(DataNotExistedException e) {
        return new ErrorResponse(e.getMessage());
    }
    
    /**
     * 유저가 존재하지 않을 때
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 빵집이 존재하지 않을 때
     */
    @ExceptionHandler(BakeryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse bakeryNotFoundException(BakeryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 빵이 존재하지 않을 때
     */
    @ExceptionHandler(BreadNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse breadNotFoundException(BreadNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 존재하는 리스트일 때
     */
    @ExceptionHandler(FlagAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagAlreadyException(FlagAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 존재하지 않은 리스트일 때
     */
    @ExceptionHandler(FlagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse flagNotFoundException(FlagNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 수정 또는 삭제할 수 없는 리스트(기본 리스트 : 가봤어요, 가고싶어요)일 때
     */
    @ExceptionHandler(FlagUnEditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagUnEditException(FlagUnEditException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 해당 리스트에 등록된 빵집일 때
     */
    @ExceptionHandler(FlagBakeryAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagBakeryAlreadyException(FlagBakeryAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 해당 리스트에 없는 빵집일 때
     */
    @ExceptionHandler(FlagBakeryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse flagBakeryNotFoundException(FlagBakeryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 존재하지 않은 리뷰일 때
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse breadReviewNotFoundException(ReviewNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 좋아요를 누른 리뷰일 때
     */
    @ExceptionHandler(ReviewLikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewLikeAlreadyException(ReviewLikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 좋아요를 취소한 리뷰일 때
     */
    @ExceptionHandler(ReviewUnlikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewUnlikeAlreadyException(ReviewUnlikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 삭제된 리뷰일 때
     */
    @ExceptionHandler(ReviewUnusedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewUnusedException(ReviewUnusedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 존재하지 않은 댓글일 때
     */
    @ExceptionHandler(ReviewCommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reviewCommentNotFoundException(ReviewCommentNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 좋아요를 누른 댓글일 때
     */
    @ExceptionHandler(ReviewCommentLikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewCommentLikeAlreadyException(ReviewCommentLikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 좋아요를 취소한 댓글일 때
     */
    @ExceptionHandler(ReviewCommentUnlikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewCommentUnlikeAlreadyException(ReviewCommentUnlikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이미 등록한 빵집일 일 때
     */
    @ExceptionHandler(DuplicateBakeryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicateBakeryException(DuplicateBakeryException e) {
        return new ErrorResponse(e.getMessage());
    }
}
