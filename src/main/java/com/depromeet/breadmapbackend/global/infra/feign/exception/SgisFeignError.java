package com.depromeet.breadmapbackend.global.infra.feign.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class SgisFeignError implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 404:
                return new SgisFeignException();
        }
        return null;
    }
}
