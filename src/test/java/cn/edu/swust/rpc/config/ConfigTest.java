package cn.edu.swust.rpc.config;


import cn.edu.swust.rpc.config.zookeeper.ZookeeperConfig;

import java.io.IOException;

/**
 * Created by Administrator on 2016/7/21.
 */
public class ConfigTest {

    public static void main(String[] args) throws IOException {
        Config config = new ZookeeperConfig();
        config.open();

        System.out.println(config.getData());

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            config.setData("ok");
        }
    }
}