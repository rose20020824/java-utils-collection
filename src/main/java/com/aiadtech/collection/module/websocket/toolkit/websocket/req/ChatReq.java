package com.aiadtech.collection.module.websocket.toolkit.websocket.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatReq {

    private Input inputs;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("query")
    private String query;

    @JsonProperty("response_mode")
    private String responseMode = "streaming";

    private String user;



    // 静态工厂方法：用于 inputVariables 场景
    public static ChatReq fromInputVariables(String query, String conversationId, String user) {
        ChatReq req = new ChatReq();
        req.inputs = new Input();
        req.inputs.inputVariables = query;
        req.query = query;
        req.conversationId = conversationId;
        req.user = user;
        return req;
    }

    // 静态工厂方法：用于 videoKeyword 场景
    public static ChatReq fromVideoKeyword(String query, String conversationId, String user) {
        ChatReq req = new ChatReq();
        req.inputs = new Input();
        req.inputs.videoKeyword = query;
        req.query = query;
        req.conversationId = conversationId;
        req.user = user;
        return req;
    }

    public static ChatReq fromStop( String user) {
        ChatReq req = new ChatReq();
        req.user = user;
        return req;
    }

    @Data
    public static class Input {

        private String inputVariables;
        private String videoKeyword;
    }
}
