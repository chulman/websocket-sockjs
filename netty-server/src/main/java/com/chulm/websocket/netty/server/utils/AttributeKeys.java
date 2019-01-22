package com.chulm.websocket.netty.server.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;

public class AttributeKeys {
    public static final AttributeKey<String> origin = AttributeKey.valueOf(HttpHeaderNames.ORIGIN.toString());
    public static final AttributeKey<String> corsHeaders = AttributeKey.valueOf(HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS.toString());
    public static final AttributeKey<String> htmlfileCallback = AttributeKey.valueOf("Htmlfile_callback");
}
