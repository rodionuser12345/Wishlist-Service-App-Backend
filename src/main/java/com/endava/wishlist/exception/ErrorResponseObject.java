package com.endava.wishlist.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseObject {
    private String message;
    private Date time;

    public ErrorResponseObject(String message) {
        this.message = message;
        this.time = new Date();
    }
}
