package cn.edu.swust.rpc.api.impl;

import cn.edu.swust.rpc.aop.ConsumerHook;
import cn.edu.swust.rpc.api.RpcConsumer;
import cn.edu.swust.rpc.async.RpcCallbackListener;
import cn.edu.swust.rpc.async.RpcResponseFuture;
import cn.edu.swust.rpc.io.Client;
import cn.edu.swust.rpc.io.ReadListener;
import cn.edu.swust.rpc.io.netty.NettyClient;
import cn.edu.swust.rpc.model.RpcRequest;
import cn.edu.swust.rpc.model.RpcResponse;
import cn.edu.swust.rpc.serializer.Serializer;
import cn.edu.swust.rpc.serializer.SerializerUtil;
import cn.edu.swust.rpc.serializer.hessian.HessianSerializer;
import cn.edu.swust.rpc.serializer.jdk.JdkSerializer;
import cn.edu.swust.rpc.serializer.json.JsonSerializer;
import cn.edu.swust.rpc.serializer.kryo.KryoSerializer;
import cn.edu.swust.rpc.serializer.protostuff.ProtostuffSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class RpcConsumerImpl implements RpcConsumer, ReadListener, InvocationHandler {
	/* 监听配置 */
	private String ip = "0.0.0.0";
	private int port = 8888;

	/* 调用超时 */
	private int timeout = 0;

	/* 接口类 */
	private Class<?> interfaceClazz;

	/* 消费者Hook */
	private ConsumerHook consumerHook;

	/* 传输接口 */
	private Client client = new NettyClient();

	/* 序列化 */
	private Serializer serializer = new KryoSerializer();

	/* 计数器 */
	private AtomicLong seqNumGenerator = new AtomicLong(0);

	/* 异步调用 */
	private ExecutorService asynService = Executors.newCachedThreadPool();

	/* 接收列表 */
	private Map<Long, BlockingQueue<RpcResponse>> respMap = new ConcurrentHashMap<>();

	/* 监听列表 */
	private Map<String, RpcCallbackListener> listenerMap = new HashMap<>();

	@Override
	public void start() throws InterruptedException {
		serializer.init();
		serializer.register(RpcRequest.class);
		serializer.register(RpcResponse.class);

		client.setClient(ip, port);
		client.setListener(this);

		client.start();
	}

	@Override
	public void close() throws InterruptedException {
		client.close();
	}

	@Override
	public void asynCall(String methodName, RpcCallbackListener callbackListener) {
		listenerMap.put(methodName, callbackListener);
	}

	@Override
	public void cancelAsyn(String methodName) {
		listenerMap.remove(methodName);
	}

	@Override
	public RpcConsumer setTimeout(int timeout) {
		if (timeout < 0) {
			return null;
		}

		this.timeout = timeout;

		return this;
	}

	@Override
	public RpcConsumer setHook(ConsumerHook consumerHook) {
		if (consumerHook == null) {
			return null;
		}

		this.consumerHook = consumerHook;

		return this;
	}

	@Override
	public RpcConsumer setSerializeType(String serializeType) {
		serializer = SerializerUtil.getSerializer(serializeType);
		if (serializer == null) {
			return null;
		}

		return this;
	}

	@Override
	public RpcConsumer setClient(String ip, int port) {
		if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
			return null;
		}

		this.ip = ip;
		this.port = port;

		return this;
	}

	@Override
	public Object instance(Class<?> interfaceClazz) {
		if (interfaceClazz == null) {
			return null;
		}

		this.interfaceClazz = interfaceClazz;

		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.interfaceClazz},this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		RpcRequest request = new RpcRequest();
		
		request.setId(seqNumGenerator.getAndAdd(1));
		request.setMethodName(method.getName());
		request.setParameterTypes(method.getParameterTypes());
		request.setArguments(arguments);
		
		RpcTask rpcTask = new RpcTask(request);
		FutureTask<Object> rpcFutureTask = new FutureTask<>(rpcTask);
		
		asynService.submit(rpcFutureTask);
		
		// 非异步调用
		if (!listenerMap.containsKey(method.getName())) {
			
			RpcResponse response = (RpcResponse) rpcFutureTask.get(timeout, TimeUnit.MILLISECONDS);
			if (response != null) {
				Object result = response.getResult();
				if (result instanceof Throwable) {  
					if (result instanceof InvocationTargetException) {
						InvocationTargetException e = (InvocationTargetException) result;
						throw e.getTargetException();
					}
					return null;
				}  

				return result;
			}
		} else {
			RpcCallbackListener listener = listenerMap.get(method.getName());
			if (listener == null) {
				RpcResponseFuture.setRpcFuture(rpcFutureTask);
			} else {
				RpcRecvThread rpcRecvThread = new RpcRecvThread(rpcFutureTask, listener);
				asynService.execute(rpcRecvThread);	
			}
		}

		return null;
	}

	@Override
	public byte[] readMessage(byte[] data) {
		RpcResponse response = serializer.toObject(data, RpcResponse.class);
		
		if (response == null) {
			return null;
		}

		if (!respMap.containsKey(response.getId())) {
			return null;
		}

		BlockingQueue<RpcResponse> recvQueue = respMap.get(response.getId());
		recvQueue.add(response);

		return null;
	}

	class RpcTask implements Callable<Object> {
		private RpcRequest request;
		
		private RpcResponse response;
		
		public RpcTask(RpcRequest request) {
			this.request = request;
		}
		
		@Override
		public RpcResponse call() throws Exception {
			if (consumerHook != null) {
				consumerHook.before(request);
			}

			BlockingQueue<RpcResponse> recvQueue = new LinkedBlockingQueue<>();
			respMap.put(request.getId(), recvQueue);
			
			byte[] data = serializer.toByteArray(request);
			if (client.writeMessage(data)) {
				response = recvQueue.take();
			}
			
			respMap.remove(request.getId());

			if (consumerHook != null) {
				consumerHook.after(request);
			}
			
			return response;
		}
	}
		
	class RpcRecvThread extends Thread {
		private FutureTask<Object> futureTask;
		
		private RpcCallbackListener listener;

		public RpcRecvThread(FutureTask<Object> futureTask, RpcCallbackListener listener) {
			this.futureTask = futureTask;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			try {				
				RpcResponse response = (RpcResponse) futureTask.get(timeout, TimeUnit.MILLISECONDS);
				if (response != null) {
					Object result = response.getResult();
					if (result != null) {
						if (listener != null) {
							listener.onResponse(result);
						}
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				if (listener != null) {
					listener.onException(e);
				}
			} catch (TimeoutException e) {
				if (listener != null) {
					listener.onTimeout();
				}
			}
		}
	}
}
