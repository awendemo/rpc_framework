package cn.edu.swust.rpc.test;

import cn.edu.swust.rpc.api.RpcConsumer;
import cn.edu.swust.rpc.api.impl.RpcConsumerImpl;
import cn.edu.swust.rpc.demo.service.HelloService;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceTest {

    public static void main(String[] args) throws IOException {
        RpcConsumer rpcConsumer = new RpcConsumerImpl();

        rpcConsumer.setClient("10.14.138.67", 8888);
        rpcConsumer.setTimeout(3000);

        try {
            rpcConsumer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final HelloService helloService = (HelloService) rpcConsumer.instance(HelloService.class);

        /* 调用次数 */
        final long callCount = 300000;

        /* 线程配置 */
        final int threadCount = Runtime.getRuntime().availableProcessors() * 2;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        /* 计数器 */
        final AtomicLong callAmount = new AtomicLong(0L);
        final CountDownLatch countDownLatch =new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();
        long endTime;

        for (int i = 0; i < threadCount; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    while (callAmount.get() < callCount) {
                        try {
                            helloService.echo("Tetst");

                            callAmount.incrementAndGet();
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (callAmount.intValue() < callCount) {
            System.out.println("Doesn't finish all invoking.".getBytes());
        } else {
            endTime = System.currentTimeMillis();

            System.out.println("TPS:" + (float) callAmount.get() / (float) (endTime - startTime) * 1000F);
        }

        System.exit(1);
    }
}
