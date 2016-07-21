package cn.edu.swust.rpc.api;

import cn.edu.swust.rpc.aop.ProviderHook;

public interface RpcProvider {
    void start() throws InterruptedException;

    void close() throws InterruptedException;

    RpcProvider setVersion(String version);

    RpcProvider setServer(String ip, int port);

    RpcProvider setHook(ProviderHook providerHook);

    RpcProvider setSerializeType(String serializeType);

    RpcProvider setService(Class<?> serviceInterface, Object serviceInstance);
}
