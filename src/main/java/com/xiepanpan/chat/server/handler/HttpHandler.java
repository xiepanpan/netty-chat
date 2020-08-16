package com.xiepanpan.chat.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

    }
}
