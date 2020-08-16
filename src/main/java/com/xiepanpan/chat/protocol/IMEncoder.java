package com.xiepanpan.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description: 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    public String encode(IMMessage msg) {
        if (msg == null) {
            return "";
        }
        String prex ="[" +msg.getCmd()+"]"+"["+msg.getTime()+"]";

        if (IMP.LOGIN.getName().equals(msg.getCmd())
                ||IMP.CHAT.getName().equals(msg.getCmd())
                ||IMP.FLOWER.getName().equals(msg.getCmd())) {
            prex+= ("["+msg.getSender()+"]");
        } else if (IMP.SYTEM.getName().equals(msg.getCmd())) {
            prex+= ("["+msg.getOnline()+"]");
        }
        if (!(msg.getContent()==null||"".equals(msg.getContent()))) {
            prex += (" - "+msg.getContent());
        }

        return prex;
    }
}
