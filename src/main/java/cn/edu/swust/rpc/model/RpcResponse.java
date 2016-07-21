package cn.edu.swust.rpc.model;

import java.io.Serializable;

public class RpcResponse implements Serializable {
	private static final long serialVersionUID = -4364536436151723421L;

	private long id;

	private String version;

    private String error;

	private Object result;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
    public Object getResult() {
        return result;
    }

	public void setResult(Object appResponse) {
		this.result = appResponse;
	}

    public String getError() {
        return error;
    }

    public void setError(String error) {
		this.error = error;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isError(){
		return error == null ? false:true;
	}
}
