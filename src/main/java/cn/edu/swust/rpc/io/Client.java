package cn.edu.swust.rpc.io;

public interface Client {
	void start() throws InterruptedException;

	void close() throws InterruptedException;

	Client setClient(String ip, int port);

	Client setListener(ReadListener listener);

	boolean writeMessage(byte[] data) throws Exception;
}
