package cn.edu.swust.rpc.demo;

import cn.edu.swust.rpc.api.RpcConsumer;
import cn.edu.swust.rpc.api.impl.RpcConsumerImpl;
import cn.edu.swust.rpc.demo.service.HelloService;

/**
 * Created by Administrator on 2016/7/20.
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        RpcConsumer rpcConsumer = new RpcConsumerImpl();

        rpcConsumer.setClient("127.0.0.1", 8888);
        rpcConsumer.setTimeout(3000);
        try {
            rpcConsumer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HelloService helloService = (HelloService) rpcConsumer.instance(HelloService.class);

        long index = 0;
        while (true) {
            System.out.println(helloService.echo("Test: " + index));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
