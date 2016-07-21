package cn.edu.swust.rpc.serializer;

public interface Serializer {
	void init();
	
	void register(Class<?> type);
	
	<T> T toObject(byte[] bytes, Class<T> type);
	
	byte[] toByteArray(Object obj);

	String getSerializeType();
}
