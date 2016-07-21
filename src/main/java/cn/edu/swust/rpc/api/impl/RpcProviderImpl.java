package cn.edu.swust.rpc.api.impl;

import java.lang.reflect.Method;

import cn.edu.swust.rpc.aop.ConsumerHook;
import cn.edu.swust.rpc.aop.ProviderHook;
import cn.edu.swust.rpc.api.RpcConsumer;
import cn.edu.swust.rpc.api.RpcProvider;
import cn.edu.swust.rpc.io.ReadListener;
import cn.edu.swust.rpc.io.Server;
import cn.edu.swust.rpc.io.netty.NettyServer;
import cn.edu.swust.rpc.model.RpcRequest;
import cn.edu.swust.rpc.model.RpcResponse;
import cn.edu.swust.rpc.serializer.Serializer;
import cn.edu.swust.rpc.serializer.SerializerUtil;
import cn.edu.swust.rpc.serializer.hessian.HessianSerializer;
import cn.edu.swust.rpc.serializer.jdk.JdkSerializer;
import cn.edu.swust.rpc.serializer.json.JsonSerializer;
import cn.edu.swust.rpc.serializer.kryo.KryoSerializer;
import cn.edu.swust.rpc.serializer.protostuff.ProtostuffSerializer;

public class RpcProviderImpl implements RpcProvider, ReadListener {
	/* 版本 */
	private String version;

	/* 监听配置 */
	private String ip = "0.0.0.0";
	private int port = 8888;

	/* 调用Hook */
	private ProviderHook providerHook;

	/* RPC接口和实现 */
	private Class<?> serviceInterface;
	private Object serviceInstance;
	
	/* 传输接口 */
	private Server server = new NettyServer();
	
	/* 序列化 */
	private Serializer serializer = new KryoSerializer();

	@Override
	public void start() throws InterruptedException {
		serializer.init();
		serializer.register(RpcRequest.class);
		serializer.register(RpcResponse.class);

		server.setServer(ip, port);
		server.setListener(this);

		server.start();

		System.out.println("Info: " + version + " SerializeType: " + serializer.getSerializeType());
	}

	@Override
	public void close() throws InterruptedException {
		server.close();
	}

	@Override
	public RpcProvider setVersion(String version) {
		if (version == null || version.isEmpty()) {
			return null;
		}

		this.version = version;

		return this;
	}

	@Override
	public RpcProvider setServer(String ip, int port) {
		if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
			return null;
		}

		this.ip = ip;
		this.port = port;

		return this;
	}

	@Override
	public RpcProvider setHook(ProviderHook providerHook) {
		if (providerHook == null) {
			return null;
		}

		this.providerHook = providerHook;

		return this;
	}

	@Override
	public RpcProvider setSerializeType(String serializeType) {
		serializer = SerializerUtil.getSerializer(serializeType);
		if (serializer == null) {
			return null;
		}

		return this;
	}

	@Override
	public RpcProvider setService(Class<?> serviceInterface, Object serviceInstance) {
		this.serviceInterface = serviceInterface;
		this.serviceInstance = serviceInstance;

		serializer.register(this.serviceInterface);

		return this;
	}
	
	@Override
	public byte[] readMessage(byte[] data) {
		if (data == null) {
			return null;
		}
		
		RpcRequest request = serializer.toObject(data, RpcRequest.class);
		
		if (request == null) {
			return null;
		}

		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] arguments = request.getArguments();

		Object result;
		RpcResponse response = new RpcResponse();

		try {
			Method method = serviceInstance.getClass().getMethod(methodName, parameterTypes);

			if (providerHook != null) {
				providerHook.before(request);
			}

			result = method.invoke(serviceInstance, arguments);

			if (providerHook != null) {
				providerHook.after(request);
			}
		} catch (Throwable t) {
			result = t;
			response.setError(t.toString());
		}

		response.setVersion(version);
		response.setId(request.getId());
		response.setResult(result);

		return serializer.toByteArray(response);
	}
}
