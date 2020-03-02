package cn.enjoy.zk.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 郑传振
 * @Date: 2020/3/2 23:05
 * @Description:
 */
public class ZKUtil {

    private ZooKeeper zk;

    public ZKUtil() {
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    /**
     * 级联查看某节点下所有节点及节点值
     *
     * @throws Exception
     * @throws
     */
    public static Map<String, String> getChildNodeAndValue(String path, ZooKeeper zk, Map<String, String> map) throws Exception {

        //看看传入的节点是否存在
        if (zk.exists(path, false) != null) {
            //存在的话将该节点的数据存放到map中，key是绝对路径，value是存放的数据
            map.put(path, new String(zk.getData(path, false, null)));
            //查看该节点下是否还有子节点
            List<String> list = zk.getChildren(path, false);
            if (list.size() != 0) {
                //遍历子节点,递归调用自身的方法
                for (String child : list) {
                    getChildNodeAndValue(path + "/" + child, zk, map);
                }
            }
        }

        return map;
    }

    /**
     * 删除一个节点，不管有有没有任何子节点
     */
    public static boolean rmr(String path, ZooKeeper zk) throws Exception {
        //看看传入的节点是否存在
        if ((zk.exists(path, false)) != null) {
            //查看该节点下是否还有子节点
            List<String> children = zk.getChildren(path, false);
            //如果没有子节点，直接删除当前节点
            if (children.size() == 0) {
                zk.delete(path, -1);
            } else {
                //如果有子节点，则先遍历删除子节点
                for (String child : children) {
                    rmr(path + "/" + child, zk);
                }
                //删除子节点之后再删除之前子节点的父节点
                rmr(path, zk);
            }
            return true;
        } else {
            //如果传入的路径不存在直接返回不存在
            System.out.println(path + " not exist");
            return false;
        }


    }

    /**
     * 级联创建任意节点
     * create znodePath data
     * create /a/b/c/xx 'xx'
     *
     * @throws Exception
     * @throws
     */
    public static boolean createZNode(String znodePath, String data, ZooKeeper zk) throws Exception {

        //看看要创建的节点是否存在
        if ((zk.exists(znodePath, false)) != null) {
            return false;
        } else {
            //获取父路径
            String parentPath = znodePath.substring(0, znodePath.lastIndexOf("/"));
            //如果父路径的长度大于0，则先创建父路径，再创建子路径
            if (parentPath.length() > 0) {
                createZNode(parentPath, data, zk);
                zk.create(znodePath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                //如果父路径的长度=0，则直接创建子路径
                zk.create(znodePath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return true;
        }
    }

    /**
     * 清空子节点
     */
    public static boolean clearChildNode(String znodePath, ZooKeeper zk) throws Exception {

        List<String> children = zk.getChildren(znodePath, false);

        for (String child : children) {
            String childNode = znodePath + "/" + child;
            if (zk.getChildren(childNode, null).size() != 0) {
                clearChildNode(childNode, zk);
            }
            zk.delete(childNode, -1);
        }

        return true;
    }
}