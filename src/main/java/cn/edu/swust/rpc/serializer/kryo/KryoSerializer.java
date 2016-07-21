package cn.edu.swust.rpc.serializer.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.edu.swust.rpc.serializer.Serializer;
import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.FastInput;
import com.esotericsoftware.kryo.io.FastOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;

public class KryoSerializer implements Serializer {

	private final Kryo kryo = new Kryo();

	@Override
	public void init() {
		kryo.setReferences(true);
		kryo.setRegistrationRequired(true);

		kryo.register(int[].class);
		kryo.register(int[][].class);
		kryo.register(int[][][].class);
		
		kryo.register(short[].class);
		kryo.register(short[][].class);
		kryo.register(short[][][].class);
		
		kryo.register(long[].class);
		kryo.register(long[][].class);
		kryo.register(long[][][].class);
		
		kryo.register(byte[].class);
		kryo.register(byte[][].class);
		kryo.register(byte[][][].class);
		
		kryo.register(char[].class);
		kryo.register(char[][].class);
		kryo.register(char[][][].class);
		
		kryo.register(byte[].class);
		kryo.register(byte[][].class);
		kryo.register(byte[][][].class);

		kryo.register(char[].class);
		kryo.register(char[][].class);
		kryo.register(char[][][].class);
		
		kryo.register(float[].class);
		kryo.register(float[][].class);
		kryo.register(float[][][].class);
		
		kryo.register(double[].class);
		kryo.register(double[][].class);
		kryo.register(double[][][].class);

		kryo.register(boolean[].class);
		kryo.register(boolean[][].class);
		kryo.register(boolean[][][].class);
		
		kryo.register(Class.class);
		kryo.register(Class[].class);
		kryo.register(Class[].class);
		
		kryo.register(Object.class);
		kryo.register(Object[].class);
		kryo.register(Object[][].class);
		
		kryo.register(String.class);
		kryo.register(String[].class);
		kryo.register(String[][].class);

		kryo.register(IllegalArgumentException.class, new JavaSerializer());
		kryo.register(InvocationTargetException.class, new JavaSerializer());
		
		kryo.register(Map.class, new MapSerializer());
		kryo.register(HashMap.class, new MapSerializer());
		kryo.register(ConcurrentHashMap.class, new MapSerializer());
		kryo.register(LinkedHashMap.class, new MapSerializer());
		
		kryo.register(List.class, new CollectionSerializer());
		kryo.register(ArrayList.class, new CollectionSerializer());
		kryo.register(LinkedList.class, new CollectionSerializer());

		kryo.register(CopyOnWriteArrayList.class, new CollectionSerializer());
	}
	
	@Override
	public void register(Class<?> type) {
		if (type == null) {
			return;
		}
				
		ClassResolver classResolver = kryo.getClassResolver();
		if (classResolver == null) {
			return;
		}
		
		if (!type.isInterface()) {
			Registration registration = classResolver.getRegistration(type);
			if (registration == null) {
				kryo.register(type);
				//kryo.register(type, new BeanSerializer<Object>(kryo, type));
			}
		}
		
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			
			Class<?>[] exceptionTypes = method.getExceptionTypes();
			if (exceptionTypes != null && exceptionTypes.length != 0) {
				for (Class<?> exceptionType : exceptionTypes) {
					kryo.register(exceptionType);
					//register(exceptionType);
				}
			}
			
			Class<?> returnType = method.getReturnType();
			if (returnType != null) {
				Registration registration = classResolver.getRegistration(returnType);
				if (registration == null) {
					kryo.register(returnType);
					//kryo.register(returnType, new BeanSerializer<Object>(kryo, returnType));
					register(returnType);
				}
			}
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes != null && parameterTypes.length != 0) {
				for (Class<?> parameterType : parameterTypes) {
					Registration registration = kryo.getRegistration(parameterType);
					if (registration == null) {
						kryo.register(parameterType);
						//kryo.register(parameterType, new BeanSerializer<Object>(kryo, parameterType));
						register(parameterType);
					}
				}
			}
		}
	}

	@Override
	public synchronized <T> T toObject(byte[] bytes, Class<T> type) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
			Input input = new FastInput(byteInput, 1024);
			return kryo.readObjectOrNull(input, type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public synchronized byte[] toByteArray(Object obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			Output output = new FastOutput(byteOutput, 1024);
			kryo.writeObjectOrNull(output, obj, obj.getClass());
			output.close();
			return byteOutput.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	@Override
	public String getSerializeType() {
		return "Kryo";
	}
}
