package com.zsw.demo.nio.channel;

import com.zsw.demo.nio.buffer.Buffers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Shaowei Zhang on 2018/11/5 21:23
 **/
@Slf4j
public class ServerSocketChannelHandler implements Runnable {

    private SocketAddress address;

    private static Charset utf8 = Charset.forName("utf-8");

    private static Random rn = new Random();

    public ServerSocketChannelHandler(int port) {
        this.address = new InetSocketAddress(port);
    }

    @Override
    public void run() {
        Selector selector;
        ServerSocketChannel ssc;

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(this.address, 100);

            selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);

            log.info("服务器已启动：{}", this.address);
        } catch (IOException e) {
            log.error("服务器启动失败");
            return;
        }

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                SelectionKey key;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {

                        if (key.isAcceptable()) {
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);

                            sc.register(selector, SelectionKey.OP_READ, new Buffers(512, 512));

                            log.info("一个客户端连接了：{}", sc.getRemoteAddress());
                        }

                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();

                            Buffers buffers = (Buffers) key.attachment();
                            ByteBuffer readBuffer = buffers.getReadBuffer();
                            ByteBuffer writeBuffer = buffers.getWriteBuffer();

                            sc.read(readBuffer);
                            readBuffer.flip();

                            CharBuffer charBuffer = utf8.decode(readBuffer);
                            log.info(Arrays.toString(charBuffer.array()));

                            readBuffer.rewind();

                            writeBuffer.put("Echo from server: ".getBytes());
                            writeBuffer.put(readBuffer);

                            readBuffer.clear();

                            // 设置 通道写事件
                            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        }


                        if (key.isWritable()) {
                            SocketChannel sc = (SocketChannel) key.channel();

                            Buffers buffers = (Buffers) key.attachment();
                            ByteBuffer writeBuffer = buffers.getWriteBuffer();
                            writeBuffer.flip();

                            int len = 0;
                            while (writeBuffer.hasRemaining()) {
                                len = sc.write(writeBuffer);
                                if (len == 0) {
                                    break;
                                }
                            }
                            writeBuffer.compact();

                            // 已经写完 取消写事件
                            if (len != 0) {
                                key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                            }
                        }
                    } catch (IOException e) {
                        log.error("客户端已中断");
                        key.cancel();
                        key.channel().close();
                    }
                }
                Thread.sleep(rn.nextInt(500));
            }
        } catch (IOException e) {
            log.error("服务器 selector 出错:{}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("服务器已被停止:{}", e.getMessage());
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                log.error("服务器关闭 selector 失败:{}", e.getMessage());
            }
            log.info("服务器已关闭 selector");
        }



    }
}
