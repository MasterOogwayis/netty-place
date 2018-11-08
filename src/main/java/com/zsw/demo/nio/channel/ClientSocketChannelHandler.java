package com.zsw.demo.nio.channel;

import com.zsw.demo.nio.buffer.Buffers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Shaowei Zhang on 2018/11/5 21:08
 **/
@Slf4j
@AllArgsConstructor
public class ClientSocketChannelHandler implements Runnable {

    private String name;

    private SocketAddress address;

    private static Charset utf8 = Charset.forName("utf-8");

    private static Random rn = new Random();

    @Override
    public void run() {
        Selector selector;

        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);

            selector = Selector.open();

            sc.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, new Buffers(512, 512));

            sc.connect(this.address);

            while (!sc.finishConnect()) {
                ;
            }
            log.info("{} 已连接到服务端", this.name);
        } catch (IOException e) {
            log.error("{} 客户端连接失败", this.name);
            return;
        }


        try {
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                int i = 1;
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    Buffers buffers = (Buffers) key.attachment();
                    ByteBuffer readBuffer = buffers.getReadBuffer();
                    ByteBuffer writeBuffer = buffers.getWriteBuffer();

                    SocketChannel sc = (SocketChannel) key.channel();

                    if (key.isReadable()) {
                        sc.read(readBuffer);
                        readBuffer.flip();

                        CharBuffer charBuffer = utf8.decode(readBuffer);

                        log.info(Arrays.toString(charBuffer.array()));

                        readBuffer.clear();
                    }


                    if (key.isWritable()) {
                        writeBuffer.put((this.name + " " + i).getBytes());
                        writeBuffer.flip();

                        while (writeBuffer.hasRemaining()) {
                            sc.write(writeBuffer);
                        }

                        writeBuffer.clear();
                        i ++;
                    }
                }
                Thread.sleep(1000 + rn.nextInt(1000));
            }
        } catch (IOException e) {
            log.error("{} 遇到连接错误: {}", this.name, e.getMessage());
        } catch (InterruptedException e) {
            log.error("{} 客户端已被终止:{}", this.name, e.getMessage());
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                log.error("{} 关闭 selector 出错", this.name);
            }
            log.info("{} 客户端已停止", this.name);
        }


    }
}
