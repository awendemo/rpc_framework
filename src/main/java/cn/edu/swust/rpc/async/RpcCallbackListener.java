package cn.edu.swust.rpc.async;

public interface RpcCallbackListener {
    void onResponse(Object response);

    void onTimeout();

    void onException(Exception e);
}
