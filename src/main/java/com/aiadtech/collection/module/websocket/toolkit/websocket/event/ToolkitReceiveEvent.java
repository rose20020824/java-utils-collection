package com.aiadtech.collection.module.websocket.toolkit.websocket.event;


import com.aiadtech.collection.constant.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端→服务端的消息结构（提问事件）
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ToolkitReceiveEvent {

    // 事件定义
    public static final String EVENT_PING = "ping";

    public static final String EVENT_STUDENT_MESSAGE = "studentMsg";

    private String event;

    /**
     * 新消息
     */
    private AiChatMessage aiChatMsg;

    public ToolkitReceiveEvent(String event) {
        this.event = event;
    }

    public ToolkitReceiveEvent(AiChatMessage aiChatMsg) {
        this(EVENT_STUDENT_MESSAGE);
        this.aiChatMsg = aiChatMsg;
    }

    /**
     * 校验参数
     */
    public ErrorCode valid() {
        if (event == null) {
            return ErrorCode.FORM_VALID_FAIL.updateMessage("event cannot be null");
        }

        return ErrorCode.OK;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiChatMessage {

        //会话id
        private String sessionId;

        //用户id
        private String customerId;

        //提问内容
        private String text;
    }
}
