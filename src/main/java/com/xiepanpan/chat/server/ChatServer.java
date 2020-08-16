package com.xiepanpan.chat.server;

import com.xiepanpan.chat.protocol.IMDecoder;
import com.xiepanpan.chat.protocol.IMEncoder;
import com.xiepanpan.chat.server.handler.HttpHandler;
import com.xiepanpan.chat.server.handler.SocketHandler;
import com.xiepanpan.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;


/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description:
 */
public class ChatServer {

    private static Logger logger = Logger.getLogger(ChatServer.class);

    private int port = 80;

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //解析自定义协议
                            pipeline.addLast(new IMDecoder());
                            pipeline.addLast(new IMEncoder());
                            pipeline.addLast(new SocketHandler());

                            //解析http请求
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(64*1024));
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpHandler());

                            //解析websocket请求
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            pipeline.addLast(new WebSocketHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            logger.info("服务已启动，监听端口"+this.port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
