package com.xiepanpan.chat.server.handler;

import com.xiepanpan.chat.processor.MsgProcessor;
import com.xiepanpan.chat.protocol.IMMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;


/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {

    private static Logger logger = Logger.getLogger(SocketHandler.class);

    private MsgProcessor processor = new MsgProcessor();

    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(),msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("socket client:有客户端连接"+processor.getAddress(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("socket client:与客户端断开连接："+cause.getMessage());
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("服务端Handler创建。。。");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        processor.logout(channel);
        logger.info("socket client:" +processor.getNickName(channel)+"离开");
    }
}
