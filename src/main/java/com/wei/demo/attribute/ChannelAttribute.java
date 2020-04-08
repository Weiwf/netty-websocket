package com.wei.demo.attribute;

import com.wei.demo.session.Session;
import io.netty.util.AttributeKey;

public interface ChannelAttribute {

    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
