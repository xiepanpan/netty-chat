package com.xiepanpan.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description: 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {

    }
}
