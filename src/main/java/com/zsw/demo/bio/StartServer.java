package com.zsw.demo.bio;

import com.zsw.demo.bio.server.Server;

import java.io.IOException;

/**
 * @author Shaowei Zhang on 2018/11/1 22:01
 **/
public class StartServer {

    public static void main(String[] args) throws IOException {

        new Server(8080).start();

    }

}
