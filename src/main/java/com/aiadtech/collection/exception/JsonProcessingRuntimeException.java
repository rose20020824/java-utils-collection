package com.aiadtech.collection.exception;


import com.aiadtech.collection.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonProcessingRuntimeException extends FatalException {

    public JsonProcessingRuntimeException(JsonProcessingException e) {
        super(ErrorCode.JSON_PROCESSING_EXCEPTION, e);
    }
}
