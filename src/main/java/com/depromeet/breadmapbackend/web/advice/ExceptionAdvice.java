package com.depromeet.breadmapbackend.web.advice;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.exception.*;
import com.depromeet.breadmapbackend.domain.exception.ImageInvalidException;
import com.depromeet.breadmapbackend.domain.exception.ImageNotExistException;
import com.depromeet.breadmapbackend.domain.flag.exception.*;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeDateException;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTypeWrongException;
import com.depromeet.breadmapbackend.domain.review.exception.*;
import com.depromeet.breadmapbackend.domain.user.exception.*;
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
     * ?????? URL ??? ???????????? ?????? ?????? ????????? ??????
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse wrongURLException(HttpServletRequest request, NoHandlerFoundException e) {
        return new ErrorResponse("Wrong URL");
    }

    /*
     * ?????? ???????????? (GET, POST, ..) ?????? ???????????? ?????? ?????? ????????? ??????
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ErrorResponse wrongMethodException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return new ErrorResponse("Wrong Method");
    }

    /*
     * ????????? Jwt ??? ??????????????? ?????? ?????? ?????? ????????? ??????
     */
    @ExceptionHandler(CAuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //
    protected ErrorResponse authenticationEntrypointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
        return new ErrorResponse("Invalid JWT");
    }

    /*
     * ????????? ?????? ???????????? ????????? ?????? ?????? ????????? ??????
     */
    @ExceptionHandler(CAccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse accessDeniedException(HttpServletRequest request, CAccessDeniedException e) {
        return new ErrorResponse("Access Denied");
    }


    /**
     * refresh ????????? ???, refresh ?????? ?????? X
     */
    @ExceptionHandler(TokenValidFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse tokenValidFailedExHandler(TokenValidFailedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * refresh ????????? ???????????? ?????? ???
     */
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse refreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ????????? ???????????? ???????????? ?????? ???
     */
    @ExceptionHandler(DataNotExistedException.class)
    public ErrorResponse DataNotExistedException(DataNotExistedException e) {
        return new ErrorResponse(e.getMessage());
    }
    
    /**
     * ????????? ???????????? ?????? ???
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ????????? ???????????? ?????? ???
     */
    @ExceptionHandler(BakeryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse bakeryNotFoundException(BakeryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ?????? ???
     */
    @ExceptionHandler(BreadNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse breadNotFoundException(BreadNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ???????????? ???
     */
    @ExceptionHandler(FlagAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagAlreadyException(FlagAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ?????? ???????????? ???
     */
    @ExceptionHandler(FlagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse flagNotFoundException(FlagNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ?????? ????????? ??? ?????? ?????????(?????? ????????? : ????????????, ???????????????)??? ???
     */
    @ExceptionHandler(FlagUnEditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagUnEditException(FlagUnEditException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ?????? ???????????? ????????? ????????? ???
     */
    @ExceptionHandler(FlagBakeryAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse flagBakeryAlreadyException(FlagBakeryAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(FlagBakeryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse flagBakeryNotFoundException(FlagBakeryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse breadReviewNotFoundException(ReviewNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(ReviewLikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewLikeAlreadyException(ReviewLikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ????????? ????????? ???
     */
    @ExceptionHandler(ReviewUnlikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewUnlikeAlreadyException(ReviewUnlikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ????????? ????????? ???
     */
    @ExceptionHandler(ReviewUnusedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewUnusedException(ReviewUnusedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(ReviewCommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reviewCommentNotFoundException(ReviewCommentNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(ReviewCommentLikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewCommentLikeAlreadyException(ReviewCommentLikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ????????? ????????? ???
     */
    @ExceptionHandler(ReviewCommentUnlikeAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reviewCommentUnlikeAlreadyException(ReviewCommentUnlikeAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ?????? ?????? ?????? ???
     */
    @ExceptionHandler(FollowNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse followNotFoundException(FollowNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ???????????? ?????? ?????? ???
     */
    @ExceptionHandler(FollowAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse followAlreadyException(FollowAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ????????? ???
     */
    @ExceptionHandler(SortTypeWrongException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse sortTypeWrongException(SortTypeWrongException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ???????????? ?????? ???
     */
    @ExceptionHandler(ImageNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse imageNotExistException(ImageNotExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ???????????? ?????? ???
     */
    @ExceptionHandler(ImageInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse imageInvalidException(ImageInvalidException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ????????? ??? ???
     */
    @ExceptionHandler(DuplicateBakeryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicateBakeryException(DuplicateBakeryException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ???????????? ?????? ???
     */
    @ExceptionHandler(BakeryReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse bakeryReportNotFoundException(BakeryReportNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ????????? ?????? ???
     */
    @ExceptionHandler(NoticeDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse noticeDateException(NoticeDateException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ????????? ???
     */
    @ExceptionHandler(NoticeTypeWrongException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse noticeTypeWrongException(NoticeTypeWrongException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ?????? ????????? ????????? ???
     */
    @ExceptionHandler(BlockAlreadyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse blockAlreadyException(BlockAlreadyException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * ???????????? ?????? ????????? ???
     */
    @ExceptionHandler(BlockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse blockNotFoundException(BlockNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
