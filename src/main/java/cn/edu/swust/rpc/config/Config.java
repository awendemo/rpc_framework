package cn.edu.swust.rpc.config;

/**
 * Created by Administrator on 2016/7/21.
 */
public interface Config {
    void open();

    void close();

    void setData(String data);

    String getData();

    Config setServer(String ip, int port);

    Config setListener(ConfigListener configListener);
}
