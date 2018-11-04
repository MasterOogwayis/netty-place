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
 * @author Shaowei Zhang on 2018/11/4 23:54
 **/
@Slf4j
@AllArgsConstructor
public class ClientSocketChannelHandle implements Runnable {

    private String name;

    private SocketAddress address;

    private static Charset utf8 = Charset.forName("utf8");

    private static Random rn = new Random();

    @Override
    public void run() {
        Selector selector = null;
        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);

            selector = Selector.open();

            // Buffers 分配的堆外内存，触发感兴趣事件后读写数据
            sc.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, new Buffers(1024, 1024));

            sc.connect(this.address);

            // TCP 3次握手
            while (!sc.finishConnect()) {
                ;
            }

            log.info("{} 已连接", this.name);
        } catch (IOException e) {
//            e.printStackTrace();
            log.error("连接服务端失败:{}", e.getMessage());
            return;
        }

        try {
            int i = 1;
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    Buffers buffers = (Buffers) key.attachment();

                    ByteBuffer readBuffer = buffers.getReadBuffer();
                    ByteBuffer writeBuffer = buffers.getWriteBuffer();

                    SocketChannel sc = (SocketChannel) key.channel();

                    // 可读状态
                    if (key.isReadable()) {
                        sc.read(readBuffer);
                        readBuffer.flip();

                        CharBuffer charBuffer = utf8.decode(readBuffer);

                        char[] chars = Arrays.copyOf(charBuffer.array(), readBuffer.position());

                        log.info(Arrays.toString(chars));

                        readBuffer.clear();
                    }


                    if (key.isWritable()) {
                        writeBuffer.put((this.name + " " + i).getBytes());
                        writeBuffer.flip();

                        sc.write(writeBuffer);
                        writeBuffer.clear();
                        i++;
                    }
                }
                Thread.sleep(1000 + rn.nextInt(1000));
            }


        } catch (IOException e) {
//            e.printStackTrace();
            log.error("{} 连接出错:{}", this.name, e.getMessage());
        } catch (InterruptedException e) {
//            e.printStackTrace();
            log.error("{} 连接已中断:{}", this.name, e.getMessage());
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
//                e.printStackTrace();
                log.error("{} 关闭 selector 失败:{}", this.name, e.getMessage());
            }
            log.info("{} selector 已关闭", this.name);
        }


    }
}
