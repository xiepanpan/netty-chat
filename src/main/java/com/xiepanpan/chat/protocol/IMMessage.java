package com.xiepanpan.chat.protocol;

import org.msgpack.annotation.Message;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:  自定义消息实体类
 */
@Message
public class IMMessage {
    /**
     * ip地址及端口
     */
    private String addr;
    /**
     * 命令类型[LOGIN]或者[SYSTEM]或者[LOGOUT]
     */
    private String cmd;
    /**
     * 命令发送时间
     */
    private long  time;
    /**
     * 当前在线人数
     */
    private int online;
    /**
     *  发送人
     */
    private String sender;
    /**
     * 接收人
     */
    private String receiver;
    /**
     * 消息内容
     */
    private String content;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
