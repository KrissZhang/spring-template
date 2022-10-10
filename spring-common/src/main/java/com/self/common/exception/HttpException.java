package com.self.common.exception;

public class HttpException extends RuntimeException {

    public HttpException(Throwable e) {
        super(e.getMessage(), e);
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable e) {
        super(message, e);
    }

}
