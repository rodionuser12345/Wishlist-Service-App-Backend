package com.endava.wishlist.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@RequiredArgsConstructor
public class CustomUserServiceException extends Exception {

    private final HttpStatus responseCode;

    public CustomUserServiceException(String message, HttpStatus code) {
        super(message);
        this.responseCode = code;
    }
}