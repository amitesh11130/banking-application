package com.monocept.exception;

import com.monocept.response.ApiResponse;
import com.monocept.response.Meta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationHeaderMissingException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleMissingAuthHeader(AuthorizationHeaderMissingException ex) {
        Meta meta = Meta.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(false)
                .description(ex.getMessage())
                .build();
        return ApiResponse.builder().meta(meta).data(null).build();
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleUnauthorized(UnAuthorizedException ex) {
        Meta meta = Meta.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(false)
                .description(ex.getMessage())
                .build();
        return ApiResponse.builder().meta(meta).data(null).build();
    }

}
