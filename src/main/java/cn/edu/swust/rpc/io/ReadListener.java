package cn.edu.swust.rpc.io;

public interface ReadListener {
	byte[] readMessage(byte[] data);
}
