package com.zsw.demo.nio.channel;

import com.zsw.demo.nio.buffer.Buffers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Shaowei Zhang on 2018/11/5 0:10
 **/
@Slf4j
public class ServerSocketChannelHandler implements Runnable {

    private SocketAddress address;

    private static Charset utf8 = Charset.forName("utf8");

    private static Random rn = new Random();

    public ServerSocketChannelHandler(int port) {
        this.address = new InetSocketAddress(port);
    }

    @Override
    public void run() {
        ServerSocketChannel ssc = null;
        Selector selector = null;

        try {
            selector = Selector.open();

            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(this.address, 100);

            ssc.register(selector, SelectionKey.OP_ACCEPT, new Buffers(1024, 1024));
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("服务器启动失败，{}", e.getMessage());
        }
        log.info("服务器已启动：{}", this.address);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {

                        if (key.isAcceptable()) {
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);

                            sc.register(selector, SelectionKey.OP_READ, new Buffers(256, 256));

                            log.info("接收到连接：{}", sc.getRemoteAddress());
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

                            writeBuffer.put("Echo from server:".getBytes());
                            writeBuffer.put(readBuffer);

                            readBuffer.clear();

                            // 设置通道写事件， 触发下面过程
                            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        }

                        if (key.isWritable()) {
                            Buffers buffers = (Buffers) key.attachment();
                            // 需要些的内容通过 读事件已经保存
                            ByteBuffer writeBuffer = buffers.getWriteBuffer();
                            writeBuffer.flip();

                            SocketChannel sc = (SocketChannel) key.channel();

                            int len = 0;
                            while (writeBuffer.hasRemaining()) {
                                len = sc.write(writeBuffer);
                                if (len == 0) {
                                    // 写完了
                                    break;
                                }
                            }
                            writeBuffer.compact();

                            // 数据全部写入底层的Socket缓冲区了
                            if (len != 0) {
                                key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                            }
                        }
                    } catch (IOException e) {
//                        e.printStackTrace();
                        log.error("客户端连接错误:{}", e.getMessage());
                       key.cancel();
                       key.channel().close();
                    }
                }
                Thread.sleep(rn.nextInt(500));
            }
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("ServerThread selector error:{}", e.getMessage());
        } catch (InterruptedException e) {
//            e.printStackTrace();
            log.error("服务端已被终止:{}", e.getMessage());
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
//                e.printStackTrace();
                log.error("服务端 selector 关闭出错:{}", e.getMessage());
            }
            log.info("服务端 selector 已关闭");
        }


    }
}
