package com.aiadtech.collection.module.websocket.toolkit.websocket.event;


import com.aiadtech.collection.constant.ErrorCode;
import com.aiadtech.collection.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务端→客户端的消息结构（回答/错误事件）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ToolkitSendEvent {

    // 事件定义
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_PONG = "pong";
    public static final String EVENT_ANSWER_TEXT = "answerText";

    // 预定义实例
    public static final ToolkitSendEvent INSTANCE_PONG = new ToolkitSendEvent(EVENT_PONG);

    private String event;

    private Error error;

    /**
     * 新消息
     */
    private AnswerText answerText;

    public ToolkitSendEvent(String event) {
        this.event = event;
    }

    public ToolkitSendEvent(ErrorCode error) {
        this(EVENT_ERROR);
        this.error = new Error(error);
    }

    public ToolkitSendEvent(AnswerText answerText) {
        this(EVENT_ANSWER_TEXT);
        this.answerText = answerText;
    }

    /**
     * 新消息
     */
    public record AnswerText(String text) {
        public static ToolkitSendEvent builder(String text) {
            return new ToolkitSendEvent(new AnswerText(text));
        }
    }

    public String toJson() {
        return JsonUtil.serializer(this);
    }

    public record Error(Integer code, String message) {
        public Error(ErrorCode errorCode) {
            this(errorCode.getCode(), errorCode.getMessage());
        }
    }
}
