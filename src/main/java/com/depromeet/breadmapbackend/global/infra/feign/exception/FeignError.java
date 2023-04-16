package com.depromeet.breadmapbackend.global.infra.feign.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignError implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            default:
                return new FeignException();
        }
//        return null;
    }
}
