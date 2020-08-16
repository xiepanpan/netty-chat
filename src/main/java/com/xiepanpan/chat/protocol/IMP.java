package com.xiepanpan.chat.protocol;

/**
 * @author: xiepanpan
 * @Date: 2020/8/16 0016
 * @Description:  自定义IM协议 instance messaging Protocol 即时通信协议
 */
public enum IMP {

    /**
     * 系统消息
     */
    SYTEM("SYSTEM"),
    /**
     * 登录指令
     */
    LOGIN("LOGIN"),
    /**
     * 登出指令
     */
    LOGOUT("LOGOUT"),
    /**
     * 聊天消息
     */
    CHAT("CHAT"),
    /**
     * 送鲜花
     */
    FLOWER("FLOWER");
    private String name;

    public static  boolean isIMP(String content) {
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
    }

    public String getName() {
        return name;
    }

    IMP(String name) {
        this.name = name;
    }
}