package com.zsw.demo.bio.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Administrator on 2018/11/1 21:35
 **/
@Slf4j
@AllArgsConstructor
public class Server {

    /**
     * 需要个容器存放所有连接到的客户端
     */
    private static List<PrintWriter> clientWriters;

    /**
     * 服务器监听端口
     */
    private int port;


    public void start() throws IOException {
        clientWriters = new ArrayList<>();
        // 创建一个线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        // 初始化一个容器 保存所有连接的客户端输出流，用于转发消息
        ServerSocket serverSocket = new ServerSocket(this.port);
        log.info("服务器启动了，等待连接......");

        // 接受客户端连接
        while (true) {
            // accept 是个阻塞方法
            Socket socket = serverSocket.accept();
            log.info("一个客户端连接了...");
            // 用于 消息转发
            clientWriters.add(new PrintWriter(new BufferedOutputStream(socket.getOutputStream())));
            // 创建一个线程 保存此客户端连接 用于接收消息并处理
            threadPool.execute(new ServerHandler(socket, this::messageHandler));
        }

    }


    /**
     * 收到消息时 将信息转发给所有以保存的客户端
     *
     * @param message
     */
    private void messageHandler(String message) {
        clientWriters.forEach(printWriter -> {
            printWriter.println(message);
            printWriter.flush();
        });
    }


}
