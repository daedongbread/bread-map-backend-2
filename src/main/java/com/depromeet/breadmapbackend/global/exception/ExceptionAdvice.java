package com.depromeet.breadmapbackend.global.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.depromeet.breadmapbackend.global.infra.feign.exception.FeignException;
import com.depromeet.breadmapbackend.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.security.SignatureException;
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
        return new ErrorResponse(500, "unknown error");
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
        return new ErrorResponse(400, "request body's field is not valid");
    }

    /*
     * Request Part validation Exception
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse requestBodyNotValidException(HttpServletRequest request, WebExchangeBindException e) {
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            log.error("error field : \"{}\", value : \"{}\", message : \"{}\"", error.getField(), error.getRejectedValue(), error.getDefaultMessage());
        }
        return new ErrorResponse(400, "request part's field is not valid");
    }

    /*
     * Request Part File Missing Exception
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse missingServletRequestPartException(HttpServletRequest request, MissingServletRequestPartException e) {
        log.error("error field : \"{}\", message : \"{}\"", e.getRequestPartName(), e.getMessage());
        return new ErrorResponse(500, "request part's file is missing.");
    }

    /*
     * Request Body wrong Exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse requestBodyWrongException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return new ErrorResponse(400, "request body is wrong");
    }

    /*
     * Parse Exception
     */
    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse parseException(HttpServletRequest request, ParseException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(500, "parse exception");
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
        return new ErrorResponse(400, "path variable or query parameter not valid");
    }

    /*
     * Path Variable missing Exception
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableMissingException(HttpServletRequest request, MissingPathVariableException e) {
        log.error("error field : \"{}\", message : \"{}\"", e.getVariableName(), e.getMessage());
        return new ErrorResponse(400, "path variable is missing");
    }

    /*
     * Path Variable Type Mismatch Exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.error("error field : \"{}\", value : \"{}\", message : \"{}\"", e.getName(), e.getValue(), e.getMessage());
        return new ErrorResponse(400, "path variable type is mismatch");
    }

    /*
     * Query Parameter missing Exception
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse pathVariableMissingException(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error("error field : \"{}\", message : \"{}\"", e.getParameterName(), e.getMessage());
        return new ErrorResponse(400, "query parameter is missing");
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(406, "not acceptable");
    }

    /*
     * 업로드된 파일이 크기 제한을 초과할 경우
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(413, "upload limit exception");
    }

    /*
     * JWT Signature Exception
     */
    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ErrorResponse signatureException(HttpServletRequest request, SignatureException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(409, "JWT signature error");
    }

    /*
     * 틀린 URL 로 접근했을 경우 발생 시키는 예외
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse wrongURLException(HttpServletRequest request, NoHandlerFoundException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(404, "wrong URL");
    }

    /*
     * 틀린 접근방식 (GET, POST, ..) 으로 접근했을 경우 발생 시키는 예외
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ErrorResponse wrongMethodException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(405, "wrong method");
    }

    /**
     * Sgis Feign Exception
     */
    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse sgisFeignException(FeignException e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(400, "SGIS Exception");
    }

    /**
     * AWS S3 Exception
     */
    @ExceptionHandler(AmazonS3Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse amazonS3Exception(AmazonS3Exception e) {
        log.error("message : \"{}\"", e.getMessage());
        return new ErrorResponse(409, "S3 Exception");
    }

    /**
     * Business Exception
     */
    @ExceptionHandler(DaedongException.class)
    public ResponseEntity<ErrorResponse> daedongException(DaedongException e) {
        return ResponseEntity
                .status(e.getDaedongStatus().getStatus())
                .body(new ErrorResponse(
                        e.getDaedongStatus().getCode(),
                        e.getDaedongStatus().getDescription()
                ));
    }
}
