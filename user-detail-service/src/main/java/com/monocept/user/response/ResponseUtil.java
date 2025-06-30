package com.monocept.user.response;

import org.springframework.http.HttpStatus;

public class ResponseUtil {
    private static ApiResponse createResponse(int code, String description, Status status, Object data) {
        Meta meta = Meta.builder().code(code).description(description).status(status).build();
        return ApiResponse.builder().meta(meta).data(data).build();
    }

    public static ApiResponse success(String description, Object data) {
        return createResponse(HttpStatus.OK.value(), description, Status.SUCCESS, data);
    }

    public static ApiResponse failed(String description, Object data) {
        return createResponse(HttpStatus.BAD_REQUEST.value(), description, Status.FAILED, data);
    }

    public static ApiResponse failed(int code, String description, Object data) {
        return createResponse(code, description, Status.FAILED, data);
    }

    public static ApiResponse failure(int code, String description, Object data) {
        return createResponse(code, description, Status.FAILURE, data);
    }
}
