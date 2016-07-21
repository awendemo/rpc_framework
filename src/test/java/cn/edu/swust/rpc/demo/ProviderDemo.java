package cn.edu.swust.rpc.demo;

import cn.edu.swust.rpc.api.RpcProvider;
import cn.edu.swust.rpc.api.impl.RpcProviderImpl;
import cn.edu.swust.rpc.demo.service.HelloService;
import cn.edu.swust.rpc.demo.service.HelloServiceImpl;

public class ProviderDemo {
    public static void main(String[] args) {
        RpcProvider rpcProvider = new RpcProviderImpl();

        rpcProvider.setVersion("1.0");
        rpcProvider.setServer("0.0.0.0", 8888);
        rpcProvider.setService(HelloService.class, new HelloServiceImpl());

        try {
            rpcProvider.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
