package com.wei.demo.server.handler;

import com.wei.demo.session.Session;
import com.wei.demo.util.WebsocketUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * Http请求处理器
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        /*HttpHeaders httpHeaders = request.headers();
        String userId = httpHeaders.get("userId");
        String userName = httpHeaders.get("userName");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName)){
            ctx.pipeline().writeAndFlush("userId或者userName为空！");
        }else{

        }*/
        System.out.println("------------- HttpRequestHandler ----------");
        Session session = new Session("123","wwf");
        WebsocketUtils.setSession(ctx.channel(),session);
        WebsocketUtils.addChannle(session.getUserId(),ctx.channel());
        ctx.fireChannelRead(request.retain());

    }
}
