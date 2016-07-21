package cn.edu.swust.rpc.serializer.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.edu.swust.rpc.serializer.Serializer;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianSerializer implements Serializer {
	@Override
	public void init() {
		
	}
	
	@Override
	public void register(Class<?> type) {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T toObject(byte[] bytes, Class<T> type) {
		if (bytes == null || bytes.length == 0 || type == null) {
			return null;
		}
		
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			HessianInput hi = new HessianInput(is);
			
			return (T) hi.readObject();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	@Override
	public byte[] toByteArray(Object obj) {
		if (obj == null) {
			return null;
		}
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			HessianOutput ho = new HessianOutput(os);
			ho.writeObject(obj);
			return os.toByteArray();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String getSerializeType() {
		return "Hession";
	}
}
