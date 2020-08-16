package com.xiepanpan.chat.server.handler;

import com.xiepanpan.chat.protocol.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {

    }
}
