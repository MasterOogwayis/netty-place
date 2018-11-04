package com.zsw.demo.nio.buffer;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author Shaowei Zhang on 2018/11/4 13:52
 **/
@Getter
public class Buffers {

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;


    public Buffers(int readCapacity, int writeCapacity) {
        this.readBuffer = ByteBuffer.allocate(readCapacity);
        this.writeBuffer = ByteBuffer.allocate(writeCapacity);
    }
}
