package org.example.backend.util;


import org.example.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return new ResponseEntity<>(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .build(),
                HttpStatus.OK
        );
    }

    public static ResponseEntity<ApiResponse<Object>> created(Object data, String message) {
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .build(),
                HttpStatus.CREATED
        );
    }

    public static ResponseEntity<ApiResponse<Object>> failure(String message, HttpStatus status) {
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build(),
                status
        );
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, int statusCode) {
        return new ResponseEntity<>(
                ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build(),
                HttpStatus.valueOf(statusCode)
        );
    }
}
