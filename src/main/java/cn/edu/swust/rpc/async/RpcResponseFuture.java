package cn.edu.swust.rpc.async;

import cn.edu.swust.rpc.model.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcResponseFuture {
    public static ThreadLocal<Future<Object>> futureThreadLocal = new ThreadLocal<>();

    public static Object getRpcResponse(long timeout) throws InterruptedException {
        if (futureThreadLocal.get() == null) {
            throw new RuntimeException("Thread [" + Thread.currentThread() + "] have not set the response future!");
        }

        try {
            RpcResponse response = (RpcResponse) (futureThreadLocal.get().get(timeout, TimeUnit.MILLISECONDS));
            if (response.isError()) {
                throw new RuntimeException(response.getError());
            }
            return response.getResult();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Time out", e);
        }
    }

    public static void setRpcFuture(Future<Object> future) {
        futureThreadLocal.set(future);
    }
}
