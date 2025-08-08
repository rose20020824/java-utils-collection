package com.aiadtech.collection.module.websocket.websocket.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websocket")
@Data
public class WebsocketProperties {

    private String address ;

    private Integer port ;

}
