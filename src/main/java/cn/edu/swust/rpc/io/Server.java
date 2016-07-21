package cn.edu.swust.rpc.io;

public interface Server {
	void start() throws InterruptedException;

	void close() throws InterruptedException;

	Server setServer(String ip, int port);

	Server setListener(ReadListener listener);
}
