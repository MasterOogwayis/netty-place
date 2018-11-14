package com.zsw.demo.ch01;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author Shaowei Zhang on 2018/11/14 0:11
 **/
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("A client has connected to server. {}", ctx.channel().remoteAddress());
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + ". \r\n");
        ctx.write("It is " + new Date() + " now. \r\n");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String response;
        boolean close = false;

        if (msg.isEmpty()) {
            response = "Please type something. \r\n";
        } else {
            if ("bye".equalsIgnoreCase(msg)) {
                response = "Have a good day. \r\n";
                close = true;
            } else {
                response = "Server has received you message '" + msg + "'. \r\n";
            }
        }
        ChannelFuture channelFuture = ctx.write(response);

        if (close) {
            log.info("A client has logout, {}", ctx.channel().remoteAddress());
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
