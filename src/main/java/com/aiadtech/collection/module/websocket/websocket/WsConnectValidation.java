package com.aiadtech.collection.module.websocket.websocket;

import io.netty.channel.ChannelHandlerContext;
import jakarta.validation.Validation;

import java.util.Map;

/**
 * websocket连接验证接口
 */
public interface WsConnectValidation {

    /**
     * Websocket建立连接时请求参数校验
     */
    boolean validate(ChannelHandlerContext ctx, Map<String, String> queryParams);

    /**
     * 验证表单是否非法
     */
    default <T> String formIllegality(T form) {
        return Validation.buildDefaultValidatorFactory()
            .getValidator()
            .validate(form)
            .stream()
            .findFirst()
            .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
            .orElse(null);
    }
}
