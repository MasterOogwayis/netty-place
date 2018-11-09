package com.zsw.demo.ch00;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shaowei Zhang on 2018/11/9 22:21
 **/
@Slf4j
public class NettyServer {

    /**
     * BOSS 线程池大小为核心数 * 2
     */
    private static final int BOSS_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final int WORK_THREAD_SIZE = 100;



    public static void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_THREAD_SIZE);
        EventLoopGroup workGroup = new NioEventLoopGroup(WORK_THREAD_SIZE);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast(new TcpServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server has got an error : {}", e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        start();
    }


}
