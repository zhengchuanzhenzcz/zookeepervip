package cn.enjoy.zk.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


public class TestApi implements Watcher {
    private final static String CONNECTSTRING = "49.235.196.15:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zookeeper;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        zookeeper = new ZooKeeper(CONNECTSTRING, 5000, new TestApi());
        countDownLatch.await();

        boolean createZNode = ZKUtil.createZNode("/x/c/z/bb", "bb", zookeeper);
        if (createZNode) {
            System.out.println("创建成功！");
        } else {
            System.out.println("创建失败");
        }
    }

    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState() + "-->" + watchedEvent.getType());
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    System.out.println("数据变更触发路径：" + watchedEvent.getPath() + "->改变后的值：" +
                            new String(zookeeper.getData(watchedEvent.getPath(), true, stat)));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {//子节点的数据变化会触发
                try {
                    System.out.println("子节点数据变更路径：" + watchedEvent.getPath() + "->节点的值：" +
                            zookeeper.getData(watchedEvent.getPath(), true, stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeCreated) {//创建子节点的时候会触发
                try {
                    System.out.println("节点创建路径：" + watchedEvent.getPath() + "->节点的值：" +
                            zookeeper.getData(watchedEvent.getPath(), true, stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {//子节点删除会触发
                System.out.println("节点删除路径：" + watchedEvent.getPath());
            }
        }

    }
}
