package com.zsw.demo.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Shaowei Zhang on 2018/11/4 17:31
 **/
public class BufferDemo {

    public static void main(String[] args) {
        encode("你好");
        decode("你好");
    }

    public static void encode(String str) {
        CharBuffer charBuffer = CharBuffer.allocate(128);
        charBuffer.append(str);
        charBuffer.flip();

        Charset utf8 = Charset.forName("utf-8");
        ByteBuffer byteBuffer = utf8.encode(charBuffer);
        byte[] bytes = Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
        System.out.println(Arrays.toString(bytes));


    }

    public static void decode(String str) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();

        Charset utf8 = Charset.forName("utf-8");
        CharBuffer charBuffer = utf8.decode(byteBuffer);

        char[] chars = Arrays.copyOf(charBuffer.array(), charBuffer.limit());
        System.out.println(chars);


    }


}
