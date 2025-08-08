package com.aiadtech.collection.module.websocket.toolkit.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenPOJO {
    private String customerId;
    private String source;
}
