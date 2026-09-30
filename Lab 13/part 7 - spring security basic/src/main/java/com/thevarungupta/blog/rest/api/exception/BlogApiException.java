package com.thevarungupta.blog.rest.api.exception;

import org.springframework.http.HttpStatus;

public class BlogApiException extends RuntimeException{
    private HttpStatus httpStatus;
    private String message;

    public BlogApiException(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public BlogApiException(String customMessage, HttpStatus httpStatus, String message){
        super(customMessage);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
