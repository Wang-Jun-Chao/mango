package mango.registry.zookeeper;

import mango.common.URL;
import mango.util.Constants;

/**
 * zookeeper工具类
 */
public class ZkUtils {

    /**
     * 获取分组路径
     *
     * @param url
     * @return
     */
    public static String toGroupPath(URL url) {
        return Constants.ZOOKEEPER_REGISTRY_NAMESPACE
                + Constants.PATH_SEPARATOR
                + url.getGroup();
    }

    /**
     * 获取服务路径
     *
     * @param url
     * @return
     */
    public static String toServicePath(URL url) {
        return toGroupPath(url)
                + Constants.PATH_SEPARATOR
                + url.getPath();
    }

    /**
     * 获取结点类型路径
     *
     * @param url
     * @param nodeType
     * @return
     */
    public static String toNodeTypePath(URL url, ZkNodeType nodeType) {
        return toServicePath(url)
                + Constants.PATH_SEPARATOR
                + nodeType.getValue();
    }

    /**
     * 获取结点路径
     *
     * @param url
     * @param nodeType
     * @return
     */
    public static String toNodePath(URL url, ZkNodeType nodeType) {
        return toNodeTypePath(url, nodeType)
                + Constants.PATH_SEPARATOR
                + url.getServerAndPort();
    }
}
