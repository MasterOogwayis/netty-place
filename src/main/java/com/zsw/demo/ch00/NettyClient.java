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
 * @author Shaowei Zhang on 2018/11/8 17:31
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
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline
                                    .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast("frameEncoder", new LengthFieldPrepender(4))
                                    .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast("handler", new MyClient());
                        }
                    });

            for (int i = 0; i < 10; i++) {
                ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
                channelFuture.channel().writeAndFlush("This is client " + Thread.currentThread().getName() + " speaking ------> " + i);
                channelFuture.channel().closeFuture().sync();
            }

        } catch (Exception e) {
            log.error("client catch a exception : {}", e);
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
