package com.depromeet.breadmapbackend.security;

import com.depromeet.breadmapbackend.web.controller.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class ExceptionController {
    @GetMapping("/entryPoint")
    public ErrorResponse getEntrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @PostMapping("/entryPoint")
    public ErrorResponse postEntrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @PutMapping("/entryPoint")
    public ErrorResponse putEntrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @PatchMapping("/entryPoint")
    public ErrorResponse patchEntrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @DeleteMapping("/entryPoint")
    public ErrorResponse deleteEntrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @GetMapping("/accessDenied")
    public ErrorResponse getAccessDeniedException() {
        throw new CAccessDeniedException();
    }

    @PostMapping("/accessDenied")
    public ErrorResponse postAccessDeniedException() {
        throw new CAccessDeniedException();
    }

    @PutMapping("/accessDenied")
    public ErrorResponse putAccessDeniedException() {
        throw new CAccessDeniedException();
    }

    @PatchMapping("/accessDenied")
    public ErrorResponse patchAccessDeniedException() {
        throw new CAccessDeniedException();
    }

    @DeleteMapping("/accessDenied")
    public ErrorResponse deleteAccessDeniedException() {
        throw new CAccessDeniedException();
    }
}
