package com.xiepanpan.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description: 自定义IM协议的解码器
 */
public class IMDecoder extends ByteToMessageDecoder {

    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");


    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array,in.readerIndex(),length);
            if (!(content==null||"".equals(content.trim()))) {
                if (!IMP.isIMP(content)) {
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }

            in.getBytes(in.readerIndex(),array,0,length);
            out.add(new MessagePack().read(array,IMMessage.class));
            in.clear();
        } catch (MessageTypeException e) {
            ctx.channel().pipeline().remove(this);
        }
    }


    public IMMessage decode(String msg) {
        if (msg==null || "".equals(msg.trim())) {
            return null;
        }

        try {
            Matcher matcher = pattern.matcher(msg);
            String header = "";
            String content = "";
            if (matcher.matches()) {
                header = matcher.group(1);
                content = matcher.group(3);
            }

            String[] headers = header.split("\\]\\[");
            long time = 0;
            try {
                time = Long.parseLong(headers[1]);
            } catch (NumberFormatException e) {
            }
            String nickname = headers[2];

            nickname = nickname.length()<10?nickname:nickname.substring(0,9);

            if (msg.startsWith("["+IMP.LOGIN.getName()+"]")) {
                return new IMMessage(headers[0],time,nickname);
            } else if (msg.startsWith("["+IMP.CHAT.getName()+"]")) {
                //聊天指令
                return new IMMessage(headers[0],time,nickname,content);
            } else if (msg.startsWith("["+IMP.FLOWER.getName()+"]")) {
                return new IMMessage(headers[0],time,nickname);
            }else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }


    }
}
