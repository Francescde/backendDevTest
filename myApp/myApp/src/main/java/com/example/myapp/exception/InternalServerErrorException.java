package com.example.myapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

public class InternalServerErrorException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(InternalServerErrorException.class);

    public InternalServerErrorException(String message) {
        super(message);
        logger.error(message);
    }
}
