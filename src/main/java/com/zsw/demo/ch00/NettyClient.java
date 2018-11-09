package com.zsw.demo.ch00;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shaowei Zhang on 2018/11/9 22:31
 **/
@Slf4j
public class NettyClient implements Runnable {
    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast(new MyClient());
                        }
                    });
            for (int i = 0; i < 10; i++) {
                ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
                channelFuture.channel().writeAndFlush("Hello Server , this is " + Thread.currentThread().getName() + " speaking ---> " + i);
                channelFuture.channel().closeFuture().sync();
            }


        } catch (InterruptedException e) {
            log.error("client has got an error : {}", e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < 3; i++) {
            new Thread(new NettyClient(), "Thread " + i).start();
        }

    }

}
