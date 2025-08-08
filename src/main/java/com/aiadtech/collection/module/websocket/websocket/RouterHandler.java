package com.aiadtech.collection.module.websocket.websocket;


import com.aiadtech.collection.module.websocket.toolkit.websocket.ToolkitWsController;
import com.aiadtech.collection.util.Slf4jMDCUtil;
import com.aiadtech.collection.util.SpringUtils;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 路由处理器，用于转发请求到对应的WebSocketHandler(多路径支持)
 */
@Slf4j
public class RouterHandler extends ChannelInboundHandlerAdapter {

    /**
     * 由于该项目中需要定义多条Websocket路由用于不同场景，但Netty一个服务只能设置一次WebSocketServerProtocolHandler
     * 因此需要将RouteKey定义给WebSocketServerProtocolHandler，并且在WebSocketServerProtocolHandler之前定义一个 RouterHandler {@link RouterHandler}
     * 判断request uri为合法路由后，将request uri重置为 RouteKey，使请求能正确转发给WebSocketServerProtocolHandler处理
     */
    public static final String ROUTE_KEY = String.valueOf(Math.random());

    /**
     * 路由列表
     * key:RoutePath -> value:Handler
     */
    private static final Map<String, Class<? extends ChannelHandlerAdapter>> routes = initRoutes();

    private ChannelHandlerAdapter handlerAdapter;

    /**
     * 初始化路由
     */
    public static Map<String, Class<? extends ChannelHandlerAdapter>> initRoutes() {
        var result = new HashMap<String, Class<? extends ChannelHandlerAdapter>>();

        result.put(ToolkitWsController.ROUTE, ToolkitWsController.class);
        WebsocketContext.initRouteRooms(ToolkitWsController.ROUTE);

        return result;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 首次连接是FullHttpRequest
        if (msg instanceof FullHttpRequest request) {
            Slf4jMDCUtil.upsert(request.headers().get(Slf4jMDCUtil.CHAIN_ID));

            var uri = UriComponentsBuilder.fromUriString(request.uri()).build();
            var handlerOptional = Optional.ofNullable(routes.get(uri.getPath())).map(SpringUtils::getBean);

            if (handlerOptional.isEmpty()) {
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
                ctx.close();
                return;
            }

            handlerAdapter = handlerOptional.get();
            request.setUri(ROUTE_KEY);
            ctx.pipeline().addAfter(WebSocketServerProtocolHandler.class.getName(), handlerAdapter.getClass().getName(), handlerAdapter);
            super.channelRead(ctx, msg);
            log.info("receive handler read, {} {}", uri.getQueryParams());
            log.info("receive handler read, {} {}", handlerAdapter.getClass().toString(), ctx.channel().id().asLongText());
            // 表单校验不通过时，3s后关闭当前连接
            if (handlerAdapter instanceof WsConnectValidation validation && !validation.validate(ctx, uri.getQueryParams().toSingleValueMap())) {
                ctx.executor().scheduleAtFixedRate(ctx::close, 3, 3, TimeUnit.SECONDS);
            }
            return;
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (handlerAdapter != null) {
            log.info("receive handler removed, {} {}", handlerAdapter.getClass().toString(), ctx.channel().id().asLongText());
            if (handlerAdapter instanceof AutoCloseable closeable) {
                closeable.close();
            }
        }
        Slf4jMDCUtil.clear();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("receive handler exception caught, {} {}", handlerAdapter.getClass().getName(), ctx.channel().id().asLongText(), cause);
        if (handlerAdapter instanceof AutoCloseable closeable) {
            closeable.close();
        }
    }
}
