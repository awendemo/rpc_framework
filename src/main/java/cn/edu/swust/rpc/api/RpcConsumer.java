package cn.edu.swust.rpc.api;

import cn.edu.swust.rpc.aop.ConsumerHook;
import cn.edu.swust.rpc.async.RpcCallbackListener;

public interface RpcConsumer {
    void start() throws InterruptedException;

    void close() throws InterruptedException;

    void asynCall(String methodName, RpcCallbackListener callbackListener);

    void cancelAsyn(String methodName);

    RpcConsumer setTimeout(int timeout);

    RpcConsumer setHook(ConsumerHook consumerHook);

    RpcConsumer setSerializeType(String serializeType);

    RpcConsumer setClient(String ip, int port);

    Object instance(Class<?> interfaceClazz);
}

