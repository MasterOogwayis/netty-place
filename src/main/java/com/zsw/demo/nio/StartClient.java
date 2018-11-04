package com.zsw.demo.nio;

import com.zsw.demo.nio.channel.ClientSocketChannelHandle;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Shaowei Zhang on 2018/11/5 0:37
 **/
public class StartClient {

    public static void main(String[] args) throws InterruptedException {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
        Thread ta = new Thread(new ClientSocketChannelHandle("Thread a", address));
        Thread tb = new Thread(new ClientSocketChannelHandle("Thread b", address));
        Thread tc = new Thread(new ClientSocketChannelHandle("Thread c", address));
        Thread td = new Thread(new ClientSocketChannelHandle("Thread d", address));

        ta.start();
        tb.start();
        tc.start();
        Thread.sleep(5 * 1000);

        ta.interrupt();
        td.start();


    }

}
