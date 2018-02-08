package mango.rpc;

import mango.common.URL;

/**
 * 服务节点对象
 */
public interface Node {

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 当前状态是否可用
     *
     * @return
     */
    boolean isAvailable();

    /**
     * 返回当前节点的描述信息
     *
     * @return
     */
    String desc();

    /**
     * 获取节点相关的URL
     *
     * @return
     */
    URL getUrl();
}
