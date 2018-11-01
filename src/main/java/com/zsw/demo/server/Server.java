package com.zsw.demo.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Administrator on 2018/11/1 21:35
 **/
@Slf4j
@AllArgsConstructor
public class Server {

    private int port;


    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        log.info("服务器启动了，等待连接......");

        // 接受客户端连接
        while (true) {
            // accept 是个阻塞方法
            Socket socket = serverSocket.accept();
            log.info("一个客户端连接了...");

            // 创建一个线程 保存此客户端连接 用于后续处理
            new Thread(new ServerHandler(socket)).start();
        }

    }


}
