package com.xiepanpan.chat.client;

import com.xiepanpan.chat.client.handler.ChatClientHandler;
import com.xiepanpan.chat.protocol.IMDecoder;
import com.xiepanpan.chat.protocol.IMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author: xiepanpan
 * @Date: 2020/8/16 0016
 * @Description: 聊天客户端
 */
public class ChatClient {

    private ChatClientHandler chatClientHandler;
    private String host;
    private int port;

    public ChatClient(String nickName) {
        this.chatClientHandler = new ChatClientHandler(nickName);
    }

    public static void main(String[] args) {
        new ChatClient("xp").connect("127.0.0.1",81);
    }

    private void connect(String host, int port) {
        this.host = host;
        this.port = port;

        EventLoopGroup workerGroup = new NioEventLoopGroup();;

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new IMDecoder());
                    socketChannel.pipeline().addLast(new IMEncoder());
                    socketChannel.pipeline().addLast(chatClientHandler);
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}