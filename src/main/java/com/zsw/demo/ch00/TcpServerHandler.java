package com.zsw.demo.ch00;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shaowei Zhang on 2018/11/10 12:43
 **/
@Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("A client has connected to server : {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Server received message from a client : {}", msg);
        ctx.channel().writeAndFlush("Echo from server : " + msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Server has got an error : {}", cause.getMessage());
    }
}
