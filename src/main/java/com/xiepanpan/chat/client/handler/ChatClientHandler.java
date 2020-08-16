package com.xiepanpan.chat.client.handler;

import com.xiepanpan.chat.protocol.IMMessage;
import com.xiepanpan.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * @author: xiepanpan
 * @Date: 2020/8/16 0016
 * @Description:
 */
public class ChatClientHandler  extends ChannelInboundHandlerAdapter {


    private Logger logger =Logger.getLogger(ChatClientHandler.class);

    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 收到消息后调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMMessage message = (IMMessage) msg;
        logger.info(message);
    }

    /**
     * 发生异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("与服务器断开连接"+cause.getMessage());
        ctx.close();
    }

    /**
     * tcp链路连接成功后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(),System.currentTimeMillis(),this.nickName);
        sendMsg(message);
        logger.info("成功连接服务器，已执行登录动作");
        session();
    }

    private void session() {
        new Thread() {
            @Override
            public void run() {
                logger.info(nickName+",您好，请在控制台输入消息内容");
                IMMessage message = null;
                Scanner scanner = new Scanner(System.in);
                do {
                    if (scanner.hasNext()) {
                        String input = scanner.nextLine();
                        if ("exit".equals(input)) {
                            message = new IMMessage(IMP.LOGOUT.getName(),System.currentTimeMillis(),nickName);
                        } else {
                            message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),nickName,input);
                        }
                    }
                } while (sendMsg(message));
                scanner.close();
            }
        }.start();
    }

    private boolean sendMsg(IMMessage message) {
        ctx.channel().writeAndFlush(message);
        logger.info("已发送至聊天面板，请继续输入");
        return message.getCmd().equals(IMP.LOGOUT)?false:true;

    }
}