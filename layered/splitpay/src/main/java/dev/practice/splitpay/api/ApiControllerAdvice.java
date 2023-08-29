package dev.practice.splitpay.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<?> bindException(BindException e) {
        return ApiResponse.of(
                null,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ApiResponse<?> headerException(MissingRequestHeaderException e) {
        return ApiResponse.of(
                null,
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> headerException(IllegalArgumentException e) {
        return ApiResponse.of(
                null,
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<?> noSuchElementException(NoSuchElementException e) {
        return ApiResponse.of(
                null,
                e.getMessage(),
                HttpStatus.OK
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> noSuchElementException(RuntimeException e) {
        return ApiResponse.of(
                null,
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
