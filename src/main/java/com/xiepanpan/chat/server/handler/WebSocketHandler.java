package com.xiepanpan.chat.server.handler;

import com.xiepanpan.chat.processor.MsgProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Logger logger = Logger.getLogger(WebSocketHandler.class);

    private MsgProcessor processor = new MsgProcessor();
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        processor.sendMsg(ctx.channel(),msg.text());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String address = processor.getAddress(channel);
        logger.info("webSocket client:"+address+"上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel client = ctx.channel();
        String address = processor.getAddress(client);
        logger.info("webSocket client:" +address+ "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String address = processor.getAddress(channel);
        logger.info("webSocket client:"+address+"异常");
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String address = processor.getAddress(channel);
        logger.info("webSocket client :" +address+"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String address = processor.getAddress(channel);
        logger.info("websocket client:"+address+"离开");
    }
}
