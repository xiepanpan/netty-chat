package com.xiepanpan.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.xiepanpan.chat.protocol.IMDecoder;
import com.xiepanpan.chat.protocol.IMEncoder;
import com.xiepanpan.chat.protocol.IMMessage;
import com.xiepanpan.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * @author: xiepanpan
 * @Date: 2020/8/16 0016
 * @Description:  处理自定义协议内容的逻辑
 */
public class MsgProcessor {

    private Logger logger = Logger.getLogger(MsgProcessor.class);

    //记录在线用户
    private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickname");
    private AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    private AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");

    //自定义解码器
    private IMDecoder decoder = new IMDecoder();
    //自定义编码器
    private IMEncoder encoder = new IMEncoder();

    public void sendMsg(Channel channel,IMMessage msg) {
        sendMsg(channel,encoder.encode(msg));
    }

    /**
     * 发送消息
     * @param client
     * @param msg
     */
    public void sendMsg(Channel client, String msg) {
        IMMessage request = decoder.decode(msg);
        if (request==null) {
            return;
        }

        String address = getAddress(client);

        if(request.getCmd().equals(IMP.LOGIN.getName())) {
            //登录

            //设置发送人
            client.attr(NICK_NAME).getAndSet(request.getSender());
            client.attr(IP_ADDR).getAndSet(address);
            onlineUsers.add(client);

            for (Channel channel: onlineUsers) {
                if (channel!=client) {
                    request = new IMMessage(IMP.SYTEM.getName(),sysTime(),onlineUsers.size(),getNickName(client)+"加入");
                } else {
                    request = new IMMessage(IMP.SYTEM.getName(),sysTime(),onlineUsers.size(),"已与服务器建立连接！");
                }
                String content = encoder.encode(request);
                logger.info("返回客户端的内容："+content);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        } else if (request.getCmd().equals(IMP.CHAT.getName())) {
            //发送聊天消息

            for (Channel channel: onlineUsers) {
                if (channel == client) {
                    request.setSender("you");
                }else {
                    request.setSender(getNickName(client));
                }

                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        } else if (request.getCmd().equals(IMP.FLOWER.getName())) {
            JSONObject attrs =getAttrs(client);
            long curentTime = sysTime();

            if (attrs!=null) {
                long lastFlowerTime = attrs.getLongValue("lastFlowerTime");

                //10秒内禁止刷花
                int seconds = 10;
                long sub = curentTime - lastFlowerTime;
                if (sub<1000 * seconds) {
                    request.setSender("you");
                    request.setCmd(IMP.SYTEM.getName());
                    request.setContent("您送鲜花太频繁，"+(seconds-Math.round(sub/1000))+"秒后再试;");
                    String content = encoder.encode(request);
                    client.writeAndFlush(new TextWebSocketFrame(content));
                    return;
                }
            }

            for (Channel channel: onlineUsers) {
                if (channel == client) {
                    request.setSender("you");
                    request.setContent("你给大家送了一波鲜花");
                    //记录最后送鲜花的时间
                    setAttrs(client,"lastFlowerTime",curentTime);
                } else {
                    request.setSender(getNickName(client));
                    request.setContent(getNickName(client)+"送来一波鲜花雨");
                }
                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }

    }

    /**
     * 设置扩展参数
     * @param client
     * @param lastFlowerTime
     * @param curentTime
     */
    private void setAttrs(Channel client, String key, Object value) {
        try {
            JSONObject jsonObject = client.attr(ATTRS).get();
            jsonObject.put(key,value);
            client.attr(ATTRS).set(jsonObject);
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);
            client.attr(ATTRS).set(jsonObject);
        }
    }

    /**
     * 获取扩展参数
     * @param client
     * @return
     */
    private JSONObject getAttrs(Channel client) {
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户昵称
     * @param client
     * @return
     */
    public String getNickName(Channel client) {
        return client.attr(NICK_NAME).get();
    }

    /**
     * 获取系统时间
     * @return
     */
    private long sysTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取用户远程ip地址
     * @param client
     * @return
     */
    public String getAddress(Channel client) {
        return client.remoteAddress().toString().replaceFirst("/","");
    }

    public void logout(Channel channel) {

        if (getNickName(channel)==null ) {
            return;
        }
        for (Channel channel1: onlineUsers) {
            IMMessage request = new IMMessage(IMP.SYTEM.getName(),sysTime(),onlineUsers.size(),getNickName(channel)+"离开");
            String content = encoder.encode(request);
            channel.writeAndFlush(new TextWebSocketFrame(content));
        }

        onlineUsers.remove(channel);
    }
}