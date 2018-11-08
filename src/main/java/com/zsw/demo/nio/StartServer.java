package com.zsw.demo.nio;

import com.zsw.demo.nio.channel.ServerSocketChannelHandler;

/**
 * @author Shaowei Zhang on 2018/11/5 21:46
 **/
public class StartServer {

    public static void main(String[] args) throws InterruptedException {

        Thread server = new Thread(new ServerSocketChannelHandler(8080));

        server.start();

        Thread.sleep(30 * 1000);

        server.interrupt();


    }

}
