package com.wei.demo.attribute;

import com.wei.demo.session.Session;
import io.netty.util.AttributeKey;

/**
 * Channel属性
 */
public interface ChannelAttribute {

    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
