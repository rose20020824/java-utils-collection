package com.aiadtech.collection.util;


import com.aiadtech.collection.constant.ErrorCode;

/**
 * 统一返回结果
 * @param code
 * @param data
 * @param msg
 */
public record ApiResponse(int code, Object data, String msg) {

    public static ApiResponse fail(ErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getMessage(), errorCode.getCode());
    }

    public static ApiResponse fail(String msg) {
        return fail(msg, -10411);
    }

    public static ApiResponse fail(String msg, int code) {
        return new ApiResponse(code, null, msg);
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(0, data, "success");
    }

    public static ApiResponse success() {
        return ApiResponse.success(null);
    }

}
