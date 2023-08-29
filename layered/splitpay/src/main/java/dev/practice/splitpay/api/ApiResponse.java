package dev.practice.splitpay.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private T data;

    private String message;
    private HttpStatus status;
    private int code;

    public ApiResponse(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.code = status.value();
    }

    public static <T> ApiResponse<T> of(T data, String message, HttpStatus httpStatus) {
        return new ApiResponse<>(data, message, httpStatus);
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus httpStatus) {
        return of(data, httpStatus.name(), httpStatus);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(data, "success", HttpStatus.OK);
    }

}
