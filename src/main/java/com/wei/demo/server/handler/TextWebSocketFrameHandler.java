package com.wei.demo.server.handler;

import com.wei.demo.config.NettyConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 处理webcoket中的文本消息
 */
@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public TextWebSocketFrameHandler() {

    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("TextWebSocketFrameHandler.channelRead0()，当前线程是：" + Thread.currentThread().getName());
        String request = ((TextWebSocketFrame) msg).text();
        System.out.println("TextWebSocketFrameHandler 服务端收到客户端的消息====>>>" + request);
        //创建TextWebSocketFrame对象，接收客户端发送过来的消息
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "=====>>>>" + request);
        NettyConfig.group.add(ctx.channel());
        ctx.writeAndFlush(tws);
    }

}
