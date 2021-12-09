package com.endava.wishlist.exception.handler;

import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.exception.ErrorResponseObject;
import com.endava.wishlist.exception.FieldErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;


@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(CustomUserServiceException.class)
    public ResponseEntity<ErrorResponseObject> handleCustomExceptions(Exception exception) {
        return new ResponseEntity<>(new ErrorResponseObject(exception.getMessage()),
                ((CustomUserServiceException) exception).getResponseCode());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldErrorMessage>> exceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<FieldErrorMessage> fieldErrorMessages = fieldErrors.stream().map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(fieldErrorMessages);
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseObject> handleLoginExceptions(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponseObject(e.getMessage()), BAD_REQUEST);

    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponseObject> handleUnknownException(Exception ex) {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return new ResponseEntity<>(new ErrorResponseObject(ex.getMessage()), METHOD_NOT_ALLOWED);
        } else if (ex instanceof NullPointerException) {
            return new ResponseEntity<>(new ErrorResponseObject("A null value has created an issue, the request could not be performed"), BAD_REQUEST);
        }

        return new ResponseEntity<>(new ErrorResponseObject("Unknown issue, please feel free to report"), BAD_GATEWAY);
    }

}