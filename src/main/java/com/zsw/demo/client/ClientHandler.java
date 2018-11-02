package com.zsw.demo.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Shaowei Zhang on 2018/11/1 22:30
 **/
@Slf4j
@AllArgsConstructor
public class ClientHandler implements Runnable {

    // 保存获取到的连接
    private Socket socket;


    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))
                ) {
            String message;
            while ((message = reader.readLine()) != null) {
                log.info("消息：{}", message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
