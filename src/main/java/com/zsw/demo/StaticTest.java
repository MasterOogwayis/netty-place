package com.zsw.demo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Shaowei Zhang on 2018/11/6 20:18
 **/
@Slf4j
public class StaticTest {

    public static void main(String[] args) throws IOException {
        System.out.println(13 & 19); // 1
        System.out.println(13 | 19); // 31
        System.out.println(~19); // 12
        System.out.println(13 ^ 19); // 30
    }

}
