package com.aiadtech.collection.module.websocket.toolkit.websocket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsocketConnectForm {
    @NotNull(message = "token不能为空")
    @NotBlank(message = "token不能为空")
    private String token;
    /**
     * 用户ID
     */
    @NotNull(message = "用户Id不能为空")
    @NotBlank(message = "用户Id不能为空")
    private String customerId;
}
