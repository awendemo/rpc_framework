package cn.edu.swust.rpc.config.zookeeper;

import cn.edu.swust.rpc.config.Config;
import cn.edu.swust.rpc.config.ConfigListener;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ZookeeperConfig implements Config {
    private int sessionTimeout = 10*1000;
    private String registerAddress = "127.0.0.1:2181";

    private String rootPath = "/";
    private String registerPath = "/rpc";

    private String data;

    private ZooKeeper zooKeeper;

    private ConfigListener configListener;

    @Override
    public void open() {
        zooKeeper = connectServer();
        if (zooKeeper != null) {
            watchNode(zooKeeper);
        }
    }

    @Override
    public void close() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zooKeeper = null;
    }

    @Override
    public void setData(String data) {
        if (zooKeeper != null) {
            if (data != null) {
                createNode(zooKeeper, data);
            }
        }
    }

    @Override
    public String getData() {
        return data;
    }

    public Config setServer(String ip, int port) {
        if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
            return null;
        }

        this.registerAddress = ip + ":" + port;

        return this;
    }

    public Config setListener(ConfigListener configListener) {
        if (configListener == null) {
            return null;
        }

        this.configListener = configListener;

        return this;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            final CountDownLatch latch = new CountDownLatch(1);

            zk = new ZooKeeper(registerAddress, sessionTimeout, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });

            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }

    private void createNode(final ZooKeeper zk, String data) {
        try {
            zk.create(registerPath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(rootPath, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zk);
                    }
                }
            });

            List<String> dataList = new ArrayList<>();

            for (String node : nodeList) {
                byte[] bytes = zk.getData(rootPath + node, false, null);
                dataList.add(new String(bytes));
            }

            int size = dataList.size();
            if (size > 0) {
                if (size == 1) {
                    data = dataList.get(0);
                } else {
                    data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                }
            }

            if (configListener != null) {
                configListener.onDataChange(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
