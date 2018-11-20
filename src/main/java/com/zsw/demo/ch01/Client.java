package com.zsw.demo.ch01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Shaowei Zhang on 2018/11/14 19:57
 **/
@Slf4j
public class Client {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            Channel channel = bootstrap.connect("127.0.0.1", 8080).sync().channel();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            ChannelFuture lastChannelFuture = null;

            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lastChannelFuture = channel.writeAndFlush(line + "\r\n");
                if ("bye".equalsIgnoreCase(line)) {
                    channel.closeFuture().sync();
                    break;
                }
            }
            if (lastChannelFuture != null) {
                lastChannelFuture.sync();
            }

        } catch (Exception e) {
            log.error("Client error. {}", e);
        } finally {
            group.shutdownGracefully();
        }
    }

}
