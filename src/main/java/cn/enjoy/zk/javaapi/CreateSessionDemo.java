package cn.enjoy.zk.javaapi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//原始api
public class CreateSessionDemo {
    private final static String CONNECTSTRING = "49.235.196.15:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        //创建zk
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //如果当前的连接状态是连接成功的，那么通过计数器去控制
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                    System.out.println(watchedEvent.getState());
                }
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                    System.out.println("数据变更路径：" + watchedEvent.getPath());

                }
            }
        });
        countDownLatch.await();
        System.out.println(zooKeeper.getState());


        //创建一个节点  节点名：节点数据：权限ACl：节点类型
        zooKeeper.create("/enjoy1", "enjoy".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //获取数据
//        Stat stat = new Stat();
//        byte[] data = zooKeeper.getData("/enjoy1", true, stat);
//        String result = new String(data);
//        System.out.println(result);


        //修改数据
        zooKeeper.getData("/enjoy1", true, null);
        zooKeeper.setData("/enjoy1", "deer2".getBytes(), -1);


        //删除节点
//        zooKeeper.delete("/enjoy1", -1);


        //获取节点 节点名：Watcher监听,一致性的，执行一次（当数据或者节点变更的时候通知客户端）
//        List<String> enjoy = zooKeeper.getChildren("/enjoy", true);
//        System.out.println(enjoy);

    }
}
