package com.depromeet.breadmapbackend.global.security.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BufferedRequestWrapper extends HttpServletRequestWrapper {
    private final ByteArrayInputStream inputStream;

    public BufferedRequestWrapper(HttpServletRequest request, String body) throws IOException {
        super(request);
        inputStream = new ByteArrayInputStream(body.getBytes());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
