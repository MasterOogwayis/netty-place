package com.zsw.demo.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 拿到连接的客户端，进行接下来的处理
 *
 * @author Administrator on 2018/11/1 21:54
 **/
@Slf4j
@AllArgsConstructor
public class ServerHandler implements Runnable {

    // 保存连接信息
    private Socket socket;

    @Override
    public void run() {
        try (
                // 拿到输入流 接受客户端输入
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter writer = new PrintWriter(this.socket.getOutputStream())
        ) {

            String info;
            while ((info = reader.readLine()) != null) {
                // 输出到控制台
                log.info("收到客户端信息：{}", info);
                writer.println("收到你发送的消息: " + info);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
