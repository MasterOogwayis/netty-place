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
 * @author Shaowei Zhang on 2018/11/8 17:15
 **/
@Slf4j
public class NettyServer {

    public static final int BOSS_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public static final int WORK_THREAD_SIZE = 100;

    public static final EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_THREAD_SIZE);

    public static final EventLoopGroup workGroup = new NioEventLoopGroup(WORK_THREAD_SIZE);


    public static void start() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast(new TcpServerHandle());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();

            log.info("server started");
        } catch (Exception e) {
            log.error("server error : {}", e);
        }
    }

    protected static void shutdown(){
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }



    public static void main(String[] args) throws Exception {
        start();
//        shutdown();
    }


}
