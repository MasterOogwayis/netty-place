package com.zsw.demo.bio;

import com.zsw.demo.bio.ui.JframeForm;

/**
 * @author Shaowei Zhang on 2018/11/1 22:45
 **/
public class Test {

    public static void main(String[] args) {

        JframeForm form = new JframeForm("测试");
        form.hidden();
        form.hideConnection();
        form.setVisible(true);
        form.setContent("服务器启动了...");

    }

}
