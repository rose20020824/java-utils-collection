package com.aiadtech.collection.module.websocket.toolkit.websocket;



import com.aiadtech.collection.constant.ErrorCode;
import com.aiadtech.collection.module.websocket.toolkit.websocket.event.ToolkitReceiveEvent;
import com.aiadtech.collection.module.websocket.toolkit.websocket.event.ToolkitSendEvent;
import com.aiadtech.collection.module.websocket.websocket.WebsocketContext;
import com.aiadtech.collection.module.websocket.websocket.WsConnectValidation;
import com.aiadtech.collection.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * WebSocket消息处理器，处理学生消息和Dify交互
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Slf4j
public class ToolkitWsController extends SimpleChannelInboundHandler<TextWebSocketFrame> implements WsConnectValidation {

    public static final String ROUTE = "/toolkit/ws";

    private Channel channel;

    /**
     * 当前连接是否活跃，若为false则不进行任何逻辑处理
     * 数据校验成功并完成初始化之后置为true
     * channelRead0() 须判断该值为 true 时再进行业务处理
     */
    private boolean activity = false;


    @Resource
    WebsocketContext websocketContext;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame frame) throws JsonProcessingException {
        if (!activity) {
            log.info("this connect is inactivity.");
            return;
        }

        log.info("toolkit channel read: {}", frame.text());
        var event = JsonUtil.deserializer(frame.text(), ToolkitReceiveEvent.class);
        Optional.ofNullable(event.valid())
            .filter(err -> err.getCode() != 0)
            .ifPresentOrElse(error -> writeAndFinish(new ToolkitSendEvent(error)), () -> {
                switch (event.getEvent()) {
                    case ToolkitReceiveEvent.EVENT_STUDENT_MESSAGE -> onNewMessage(event.getAiChatMsg());
                    case ToolkitReceiveEvent.EVENT_PING -> onPing();
                }
            });
    }

    /**
     * 回应心跳包
     */
    private void onPing() {
        writeAndFlush(ToolkitSendEvent.INSTANCE_PONG);
    }

    @Override
    public boolean validate(ChannelHandlerContext ctx, Map<String, String> queryParams) {
        log.info("toolkit channel validate: {}", ctx.channel().id().asLongText());
        this.channel = ctx.channel();
        var form = JsonUtil.convertValue(queryParams, WebsocketConnectForm.class);
        var formError = formIllegality(form);
        if (formError != null) {
            writeAndFinish(new ToolkitSendEvent(ErrorCode.JSAPI_WS_UNPROCESSABLE_ENTITY.updateMessage(formError)));
            return false;
        }

//        var token = authService.getTokenAndRenewal(form.getToken());
        Object token  = new Object();

        if (token == null) {
            log.info("toolkit ticket not found: {}", form.getToken());
            writeAndFinish(new ToolkitSendEvent(ErrorCode.JSAPI_WS_UNAUTHORIZED));
            return false;
        }



        initConnection(ctx, token, form);
        return true;
    }

    /**
     * 初始化连接
     */
    private void initConnection(ChannelHandlerContext ctx, Object token, WebsocketConnectForm form) {
        websocketContext.createRoom(ROUTE, WebsocketContext.roomId(null, form.getCustomerId()), ctx.channel());
        activity = true;
    }

    /**
     * 向客户端发送结束语，并将连接标为结束
     * 注意：结束表示 activity=false，不能立马关闭ws连接，否则客户端可能无法收到结束语
     */
    private void writeAndFinish(ToolkitSendEvent event) {
        writeAndFlush(event);
        activity = false;
    }

    /**
     * 发送事件消息
     */
    private void writeAndFlush(ToolkitSendEvent event) {
        channel.writeAndFlush(new TextWebSocketFrame(JsonUtil.serializer(event)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("toolkit websocket exception", cause);
    }

    private void onNewMessage(ToolkitReceiveEvent.AiChatMessage aiChatMsg) {
      //执行业务数据
    }
}
