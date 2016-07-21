package cn.edu.swust.rpc.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RpcRequest implements Serializable {
	private static final long serialVersionUID = 7503463583529418143L;
	    
	private long id;
	
	private String methodName;
	
	private Class<?>[] parameterTypes;
	
	private Object[] arguments;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
}
