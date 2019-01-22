package com.chulm.websocket.netty.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Random;

public class WebsocketResponse {

    public static ByteBuf getInfoResponse() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("{");
        sb.append("\"websocket\": ");
        //        sb.append(metadata.isWebSocketEnabled());
        sb.append(true);
        sb.append(", ");
        sb.append("\"origins\": [\"*:*\"], ");
        sb.append("\"cookie_needed\": ");
        //        sb.append(metadata.isCookieNeeded());
        sb.append(false);
        sb.append(", ");
        sb.append("\"entropy\": ");
        sb.append(new Random().nextInt(Integer.MAX_VALUE) + 1);
        sb.append("}");
        //        return Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
        return Unpooled.copiedBuffer("{\"websocket\":true,\"origins\":[\"*:*\"],\"cookie_needed\":false,\"entropy\":3496992730}", CharsetUtil.UTF_8);
    }
}
