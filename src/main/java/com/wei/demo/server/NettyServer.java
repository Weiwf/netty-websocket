package com.wei.demo.server;

import com.wei.demo.server.handler.HttpRequestHandler;
import com.wei.demo.server.handler.NettyConnectManageHandler;
import com.wei.demo.server.handler.TextWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NettyServer {
    private ServerBootstrap boot;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    @Value("${netty.server.idletime}")
    private Integer nettyServerIdleTime ;

    @Value("${netty.server.port}")
    private Integer nettyServerPort;

    @Value("${websocket.uri}")
    private String websocketUri;

    @PostConstruct
    private void start() {
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        boot = new ServerBootstrap();
        boot.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, nettyServerIdleTime, TimeUnit.SECONDS))
                                .addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpObjectAggregator(65535))
                                .addLast(new HttpRequestHandler())
                                .addLast(new WebSocketServerProtocolHandler("/websocket"))
                                .addLast(new NettyConnectManageHandler())
                                .addLast(new TextWebSocketFrameHandler())
                        ;
                    }
                });
        try {
            ChannelFuture channelFuture = boot.bind(nettyServerPort).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("NettyServer启动成功，端口是：{}，等待客户端连接......", nettyServerPort);
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("NettyServer bind port Exception", e);
        }
    }

    public void shutdown() {
        try {
            //优雅的退出
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("NettyServer shutdown exception, ", e);
        }
    }

}
