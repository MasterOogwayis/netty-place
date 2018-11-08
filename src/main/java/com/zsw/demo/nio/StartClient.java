package com.zsw.demo.nio;

import com.zsw.demo.nio.channel.ClientSocketChannelHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Shaowei Zhang on 2018/11/5 21:44
 **/
public class StartClient {

    public static void main(String[] args) throws InterruptedException {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
        Thread ta = new Thread(new ClientSocketChannelHandler("Thread a", address));
        Thread tb = new Thread(new ClientSocketChannelHandler("Thread b", address));
        Thread tc = new Thread(new ClientSocketChannelHandler("Thread c", address));
        Thread td = new Thread(new ClientSocketChannelHandler("Thread d", address));

        ta.start();
        tb.start();
        tc.start();

        Thread.sleep(5 * 1000);

        ta.interrupt();
        td.start();

    }

}
