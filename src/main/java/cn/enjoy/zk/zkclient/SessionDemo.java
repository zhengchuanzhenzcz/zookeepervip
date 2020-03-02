package cn.enjoy.zk.zkclient;

import org.I0Itec.zkclient.ZkClient;

//连接
public class SessionDemo {

    private final static String CONNECTSTRING = "49.235.196.15:2181";

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient(CONNECTSTRING, 4000);

        System.out.println(zkClient + " - > success");
    }
}
