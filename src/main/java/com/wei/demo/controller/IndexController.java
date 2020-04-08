package com.wei.demo.controller;

import com.wei.demo.config.NettyConfig;
import com.wei.demo.server.NettyServer;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class IndexController {

    @RequestMapping(value = "/websocket", method = RequestMethod.GET)
    public String index() {

        return "websocket";
    }

    @GetMapping("/sendmsg")
    @ResponseBody
    public String send(String msg) {
        TextWebSocketFrame tws = new TextWebSocketFrame(msg);

        //群发，服务端向每个连接上来的客户端群发消息
        NettyConfig.group.writeAndFlush(tws);
        return "发送成功！";
    }

}
