package com.aiadtech.collection.exception;


import com.aiadtech.collection.constant.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final Integer code;

    public AppException(ErrorCode text) {
        super(text.getMessage());
        this.code = text.getCode();
    }

    public AppException(ErrorCode text,String sxpandMessage) {
        super(text.getMessage()+sxpandMessage);
        this.code = text.getCode();
    }

    public AppException(String message) {
        super(message);
        this.code = ErrorCode.UNKNOWN_ERROR.getCode();
    }
}
