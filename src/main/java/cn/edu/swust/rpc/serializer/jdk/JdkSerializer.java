package cn.edu.swust.rpc.serializer.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.edu.swust.rpc.serializer.Serializer;

public class JdkSerializer implements Serializer {
	@Override
	public void init() {
		
	}   
	
	@Override
	public void register(Class<?> type) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toObject(byte[] bytes, Class<T> type) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		
		Object obj = null;      
		try {        
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);        
			ObjectInputStream ois = new ObjectInputStream (bis);        
			obj = ois.readObject();      
			ois.close();   
			bis.close();   
		} catch (IOException ex) {        
			ex.printStackTrace();   
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();   
		} 
		
		return (T) obj;    
	}
	
	@Override
	public byte[] toByteArray(Object obj) {
		if (obj == null) {
			return null;
		}
		
		byte[] bytes = null;      
		ByteArrayOutputStream bos = new ByteArrayOutputStream();      
		try {        
			ObjectOutputStream oos = new ObjectOutputStream(bos);         
			oos.writeObject(obj);        
			oos.flush();         
			bytes = bos.toByteArray();      
			oos.close();         
			bos.close();        
		} catch (IOException ex) {        
			ex.printStackTrace(); 
		}
		
		return bytes;    
	}

	@Override
	public String getSerializeType() {
		return "Jdk";
	}
}
