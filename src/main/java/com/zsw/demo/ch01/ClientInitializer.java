package com.zsw.demo.ch01;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shaowei Zhang on 2018/11/14 20:02
 **/
@Slf4j
public class ClientInitializer extends ChannelInitializer<NioSocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();

    private static final StringEncoder ENCODER = new StringEncoder();

    private static final ClientHandler CLIENT_HANDLER = new ClientHandler();


    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
//                .addLast(new DelimiterBasedFrameDecoder(8 * 1024, Delimiters.lineDelimiter()))
                // 回车换行符
                .addLast(new LineBasedFrameDecoder(64))
                .addLast(DECODER)
                .addLast(ENCODER)
                .addLast(CLIENT_HANDLER);
    }
}
