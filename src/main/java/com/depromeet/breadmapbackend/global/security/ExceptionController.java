package com.depromeet.breadmapbackend.global.security;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/exception")
public class ExceptionController {
    @GetMapping("/entryPoint")
    public ErrorResponse getEntrypointException() {
        throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
    }

    @PostMapping("/entryPoint")
    public ErrorResponse postEntrypointException() {
        throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
    }

    @PutMapping("/entryPoint")
    public ErrorResponse putEntrypointException() {
        throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
    }

    @PatchMapping("/entryPoint")
    public ErrorResponse patchEntrypointException() {
        throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
    }

    @DeleteMapping("/entryPoint")
    public ErrorResponse deleteEntrypointException() {
        throw new DaedongException(DaedongStatus.CUSTOM_AUTHENTICATION_ENTRYPOINT);
    }

    @GetMapping("/accessDenied")
    public ErrorResponse getAccessDeniedException() {
        throw new DaedongException(DaedongStatus.CUSTOM_ACCESS_DENIED);
    }

    @PostMapping("/accessDenied")
    public ErrorResponse postAccessDeniedException() {
        throw new DaedongException(DaedongStatus.CUSTOM_ACCESS_DENIED);
    }

    @PutMapping("/accessDenied")
    public ErrorResponse putAccessDeniedException() {
        throw new DaedongException(DaedongStatus.CUSTOM_ACCESS_DENIED);
    }

    @PatchMapping("/accessDenied")
    public ErrorResponse patchAccessDeniedException() {
        throw new DaedongException(DaedongStatus.CUSTOM_ACCESS_DENIED);
    }

    @DeleteMapping("/accessDenied")
    public ErrorResponse deleteAccessDeniedException() {
        throw new DaedongException(DaedongStatus.CUSTOM_ACCESS_DENIED);
    }
}
