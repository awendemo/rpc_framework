package cn.edu.swust.rpc.io.netty;

import cn.edu.swust.rpc.io.ReadListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import cn.edu.swust.rpc.io.Server;

public class NettyServer implements Server {
	private String ip = "0.0.0.0";
	private int port = 8080;

	private int bossGroupThreads = Runtime.getRuntime().availableProcessors();
	private int workerGroupThreads = 4;
	private int backlogSize = 128;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private ServerBootstrap serverBootstrap;

	private Channel channel;

	private ReadListener listener;

	@Override
	public void start() throws InterruptedException {
		bossGroup = new NioEventLoopGroup(bossGroupThreads);
		workerGroup = new NioEventLoopGroup(workerGroupThreads);

		serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup);
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.option(ChannelOption.SO_BACKLOG, backlogSize);
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childHandler(new NettyChannelInitializer(listener));

		channel = serverBootstrap.bind(ip, port).sync().channel();
	}

	@Override
	public void close() throws InterruptedException {
		if (channel != null) {
			channel.close().sync();
			channel = null;
		}
	}

	@Override
	public Server setServer(String ip, int port) {
		if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
			return null;
		}

		this.ip = ip;
		this.port = port;

		return this;
	}

	@Override
	public Server setListener(ReadListener listener) {
		if (listener == null) {
			return null;
		}

		this.listener = listener;

		return this;
	}
}
