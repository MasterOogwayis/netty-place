package com.zsw.demo;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * @author Shaowei Zhang on 2018/11/6 20:18
 **/
@Slf4j
public class StaticTest {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("C:\\Users\\Administrator\\Desktop\\test.txt", "rw");

        ByteBuffer writeBuffer = ByteBuffer.allocate(2 * 1024);

        for (int i = 0; i < 200; i++) {
            writeBuffer.put(String.valueOf(i).getBytes());
        }
        writeBuffer.flip();
        FileChannel fileChannel = file.getChannel();

        while (writeBuffer.hasRemaining()) {
            fileChannel.write(writeBuffer);
        }

//        fileChannel.truncate(10);
        fileChannel.force(true);

        writeBuffer.clear();
    }

}
