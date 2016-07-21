package cn.edu.swust.rpc.serializer.protostuff;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.edu.swust.rpc.serializer.Serializer;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {
	private Map<String, Schema<Object>> schemaMap = new ConcurrentHashMap<String, Schema<Object>>();

	@Override
	public void init() {
	}

	@Override
	public void register(Class<?> type) {
		if (type == null) {
			return;
		}

		addSchemaMap(type);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T toObject(byte[] bytes, Class<T> type) {
		if(bytes == null || bytes.length == 0) {
			return null;
		}

		Schema<Object> schema = schemaMap.get(type.getName());

		if (schema == null) {
			schema = addSchemaMap(type);
		}

		try {
			Object obj = type.newInstance();
			ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public byte[] toByteArray(Object obj) {
		if(obj == null) {
			return null;
		}

		Schema<Object> schema = schemaMap.get(obj.getClass().getName());

		if (schema == null) {
			schema = addSchemaMap(obj.getClass());
		}

		LinkedBuffer buffer = LinkedBuffer.allocate(4096);
		try {
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getSerializeType() {
		return "Protostuff";
	}

	@SuppressWarnings("unchecked")
	private Schema<Object> addSchemaMap(Class<?> type) {
		if (type == null) {
			return null;
		}
		
		if (type.isInterface()) {
			return null;
		}
		
		Schema<Object> schema = (Schema<Object>) RuntimeSchema.getSchema(type);
		if (schema != null) {
			schemaMap.put(type.getName(), schema);
		}

		return schema;
	}

}
