package cn.edu.swust.rpc.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import cn.edu.swust.rpc.io.Client;
import cn.edu.swust.rpc.io.ReadListener;

public class NettyClient implements Client {
	private String ip = "0.0.0.0";
	private int port = 8080;

	private int workerGroupThreads = Runtime.getRuntime().availableProcessors();

	private EventLoopGroup workerGroup;

	private Bootstrap bootstrap;

	private Channel channel;

	private ReadListener listener;
	
	@Override
	public void start() throws InterruptedException {
		workerGroup = new NioEventLoopGroup(workerGroupThreads);

		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new NettyChannelInitializer(listener));

		channel = bootstrap.connect(ip, port).sync().channel();
	}

	@Override
	public void close() throws InterruptedException {
		if (channel != null) {
			channel.close().sync();
			channel = null;
		}
	}

	@Override
	public Client setClient(String ip, int port) {
		if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
			return null;
		}

		this.ip = ip;
		this.port = port;

		return this;
	}
	
	@Override
	public Client setListener(ReadListener listener) {
		if (listener == null) {
			return null;
		}

		this.listener = listener;

		return this;
	}

	@Override
	public boolean writeMessage(byte[] data) throws Exception {
		if (channel != null && data != null) {
			channel.writeAndFlush(data).sync();
			return true;
		}

		return false;
	}
}
