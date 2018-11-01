package com.zsw.demo;

import com.zsw.demo.client.Client;

/**
 * @author Administrator on 2018/11/1 22:02
 **/
public class StartClient {

    public static void main(String[] args) {
        new Client("localhost", 8080).start();
    }

}
