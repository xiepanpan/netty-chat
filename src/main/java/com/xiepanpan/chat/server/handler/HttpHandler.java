package com.xiepanpan.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author: xiepanpan
 * @Date: 2020/8/14
 * @Description: 处理服务端分发请求的逻辑
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = Logger.getLogger(HttpHandler.class);

    private URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();

    private final String webRoot = "webRoot";

    private File getResource(String fileName) throws URISyntaxException {
        String path = baseURL.toURI() + webRoot + "/" + fileName;
        path = !path.contains("file:") ? path : path.substring(5);
        path = path.replaceAll("//","/");
        return new File(path);
    }

    /**
     * 当Channel中有新的事件消息会自动调用
     * @param ctx
     * @param request
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.getUri();

        RandomAccessFile file = null;

        try {
            String page = uri.equals("/") ? "chat.html" : uri;
            file = new RandomAccessFile(getResource(page),"r");
        } catch (Exception e) {
            ctx.fireChannelRead(request.retain());
            return;
        }

        HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if (uri.endsWith(".css")) {
            contextType = "text/css;";
        } else if (uri.endsWith(".js")) {
            contextType = "text/javascript;";
        } else if (uri.toLowerCase().matches("(jpg|png|gif)$")) {
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/"+ext;
        }

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,contextType+"charset=utf-8;");
        boolean keepAlive = HttpHeaders.isKeepAlive(request);

        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);

        ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));

        ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        logger.info("client:"+channel.remoteAddress()+"异常");
        ctx.close();
    }
}
