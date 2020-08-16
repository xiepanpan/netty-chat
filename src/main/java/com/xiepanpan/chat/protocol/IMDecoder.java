package com.xiepanpan.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description: 自定义IM协议的解码器
 */
public class IMDecoder extends ByteToMessageDecoder {


    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
