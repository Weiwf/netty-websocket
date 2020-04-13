package com.wei.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NettyServerConfig {

    @Value("${netty.server.idletime}")
    private Integer nettyServerIdleTime;

    @Value("${netty.server.port}")
    private Integer nettyServerPort;

    @Value("${websocket.uri}")
    private String websocketUri;

    private int eventWorkThreads = 4;
}
