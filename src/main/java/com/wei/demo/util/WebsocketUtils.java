package com.wei.demo.util;

import com.wei.demo.attribute.ChannelAttribute;
import com.wei.demo.session.Session;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webscoket工具类
 */
public class WebsocketUtils {

    private static final Map<String , Channel> userIdChannelMap = new ConcurrentHashMap<>(16);

    /**
     * 添加用户标识和Channel的关系
     * @param userId
     * @param channel
     */
    public static void addChannle(String userId,Channel channel){
        userIdChannelMap.put(userId,channel);
    }

    /**
     * 根据用户标识获取Channel
     * @param userId
     * @return
     */
    public static  Channel getChannel(String userId){
        return userIdChannelMap.get(userId);
    }

    /**
     * 根据用户标识删除Channel
     * @param userId
     */
    public static void removeChannel(String userId){
        userIdChannelMap.remove(userId);
    }

    /**
     * 在Channel中设置Session
     * @param channel
     * @param session
     */
    public static void setSession(Channel channel,Session session){
        channel.attr(ChannelAttribute.SESSION).set(session);
    }

    /**
     * 从Channel中获取Session
     * @param channel
     * @return
     */
    public static Session getSession(Channel channel){
        return channel.attr(ChannelAttribute.SESSION).get();
    }

    /**
     * 关闭连接
     * @param channel
     */
    public static void closeChannel(Channel channel) {
        Session session = WebsocketUtils.getSession(channel);
        WebsocketUtils.removeChannel(session.getUserId());
        channel.close();
    }
}
