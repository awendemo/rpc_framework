package cn.edu.swust.rpc.aop;

import cn.edu.swust.rpc.model.RpcRequest;

public interface ProviderHook {
    void before(RpcRequest request);
    
    void after(RpcRequest request);
}
