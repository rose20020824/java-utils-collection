package com.aiadtech.collection.module.websocket.websocket;

import com.aiadtech.collection.module.websocket.websocket.property.WebsocketProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Netty服务启动配置
 */
@Component
@Slf4j
public class WebsocketStartupConfiguration implements InitializingBean, DisposableBean {

    private final ServerInitializer serverInitializer = new ServerInitializer();

    @Resource
    private WebsocketProperties websocketProperties;

    private EventLoopGroup mainGroup;

    private EventLoopGroup workGroup;

    private ChannelFuture channelFuture;

    @Override
    public void afterPropertiesSet() {
        // 一个主线程组
        mainGroup = new NioEventLoopGroup();
        //一个工作线程组
        workGroup = new NioEventLoopGroup();
        var serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, workGroup)
            //设置队列大小
            .option(ChannelOption.SO_BACKLOG, 2048)
            .channel(NioServerSocketChannel.class)
            // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            //添加自定义初始化处理器
            .childHandler(serverInitializer);
        channelFuture = serverBootstrap.bind(websocketProperties.getAddress(), websocketProperties.getPort());

        log.info("startup websocket server bind " + websocketProperties.getAddress() + ":" + websocketProperties.getPort());
    }

    @Override
    public void destroy() {
        log.info("Shutting down Netty server...");
        if (mainGroup != null && !mainGroup.isShuttingDown() && !mainGroup.isTerminated()) {
            mainGroup.shutdownGracefully();
        }
        if (workGroup != null && !workGroup.isShuttingDown() && !workGroup.isTerminated()) {
            workGroup.shutdownGracefully();
        }
        if (channelFuture != null && channelFuture.isSuccess()) {
            channelFuture.channel().closeFuture();
        }
        log.info("Netty server shutdown.");
    }

    public static class ServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new HttpServerCodec());

            // 添加对于读写大数据流的支持
            pipeline.addLast(new ChunkedWriteHandler());

            // 对httpMessage进行聚合
            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

            // 自定义handler
            pipeline.addLast(new RouterHandler());

            pipeline.addLast(WebSocketServerProtocolHandler.class.getName(), new WebSocketServerProtocolHandler(RouterHandler.ROUTE_KEY, null, true));
        }
    }
}
