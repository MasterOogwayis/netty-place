package com.zsw.demo.bio.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * 拿到连接的客户端，进行接下来的处理
 *
 * @author Administrator on 2018/11/1 21:54
 **/
@Slf4j
@AllArgsConstructor
class ServerHandler implements Runnable {

    // 保存连接信息
    private Socket socket;

    /**
     * 定义一个方法，服务器收到消息时触发
     */
    private Consumer<String> function;

    @Override
    public void run() {
        try (
                // 拿到输入流 接受客户端输入
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))
        ) {

            String info;
            while ((info = reader.readLine()) != null) {
                // 输出到控制台
                log.info("客户端信息：{}", info);
                this.function.accept(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
