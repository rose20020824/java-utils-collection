package com.aiadtech.collection.exception;


import com.aiadtech.collection.constant.ErrorCode;

public class FatalException extends RuntimeException {
    private final ErrorCode errorCode;

    public FatalException(Throwable e) {
        super(e);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }

    public FatalException(ErrorCode errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
