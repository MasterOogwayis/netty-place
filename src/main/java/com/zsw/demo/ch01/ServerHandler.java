package com.zsw.demo.ch01;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author Shaowei Zhang on 2018/11/14 19:49
 **/
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    public static final String LF = " \r\n";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("A client has connected to server. {}", ctx.channel().remoteAddress());
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "." + LF);
        ctx.write("It is " + new Date() + " now." + LF);
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "Please type something.";
        } else if ("bye".equalsIgnoreCase(msg)) {
            response = "Have a good day.";
            close = true;
        } else {
            response = "Server has received you message '" + msg + "'.";
        }
        response += LF;
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (close) {
            log.info("A client logout. {}", channelFuture.channel().remoteAddress());
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
