package com.wei.demo.server;

import com.wei.demo.config.NettyServerConfig;
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
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class NettyServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    @Autowired
    private NettyServerConfig nettyServerConfig;

    @PostConstruct
    private void init() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getEventWorkThreads(), new ThreadFactory() {

            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerEventWorkThread_" + this.threadIndex.incrementAndGet());
            }
        });

        start();
    }

    private void start() {
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        log.info("NettyServer.initChannel()，当前线程是：" + Thread.currentThread().getName());

                        pipeline.addLast(new IdleStateHandler(0, 0, nettyServerConfig.getNettyServerIdleTime(), TimeUnit.SECONDS))
                                .addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new HttpObjectAggregator(65535))
                                .addLast(new HttpRequestHandler())
                                .addLast(new WebSocketServerProtocolHandler("/websocket"));
                        pipeline.addLast(defaultEventExecutorGroup,
                                new NettyConnectManageHandler(),
                                new TextWebSocketFrameHandler())
                        ;
                    }
                });
        try {
            ChannelFuture channelFuture = boot.bind(nettyServerConfig.getNettyServerPort()).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("NettyServer启动成功，端口是：{}，等待客户端连接......", nettyServerConfig.getNettyServerPort());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("NettyServer bind port Exception", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            //优雅的退出
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("NettyServer shutdown exception, ", e);
        }
    }

}
