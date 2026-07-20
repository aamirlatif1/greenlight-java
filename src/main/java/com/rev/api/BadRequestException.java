package com.rev.api;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(400, message);
    }
}
