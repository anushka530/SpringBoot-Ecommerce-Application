package com.ecommerce.sb_ecom.exception;

public class APIException extends RuntimeException{
    private static final Long serialVersionUID=1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
