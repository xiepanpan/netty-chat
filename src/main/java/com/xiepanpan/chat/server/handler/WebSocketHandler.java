package com.xiepanpan.chat.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }
}
