package com.zsw.demo.bio.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * @author Administrator on 2018/11/1 22:02
 */
@Slf4j
@AllArgsConstructor
public class Client {

    private String address;

    private Integer port;


    /**
     * 启动客户端
     */
    public void start() {
        try (
                Socket socket = new Socket(this.address, this.port);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                // BufferReader 效率比Scanner高
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
        ) {

            // 创建一个线程 用于读取服务器发来的消息
            new Thread(new ClientHandler(socket)).start();
            // 控制台输入 发到服务器
            String message;
            while ((message = reader.readLine()) != null) {
                // write 不会写入换行符，另一端使用 readLine方法，由于读不到换行符的结束标志，
                // 由于IO流是阻塞的，所以一直卡着不动，没有反应
                // println 就自动加上了
//                writer.write(message + "\r\n");
                writer.println(message);
                // 坑了我半天，为何服务端收不到消息！
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
