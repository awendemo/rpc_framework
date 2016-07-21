package cn.edu.swust.rpc.demo.service;

public class HelloServiceImpl implements HelloService {
	@Override
	public String echo(String value) {
		return value;
	}
}
