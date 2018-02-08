package mango.transport;

import mango.common.URL;

import java.net.InetSocketAddress;

/**
 * 端点类
 */
public interface Endpoint {

    /**
     * 获取本机套接节地址
     *
     * @return local address.
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取远程套接字地址
     *
     * @return
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 打开连接
     *
     * @return
     */
    boolean open();

    /**
     * 判断端点是否可用
     *
     * @return
     */
    boolean isAvailable();

    /**
     * 判断端点是否关闭
     *
     * @return
     */
    boolean isClosed();

    /**
     * 获取服务的URL
     *
     * @return
     */
    URL getUrl();

    /**
     * 关闭端点
     */
    void close();

    /**
     * 优雅关闭通信连接
     */
    void close(int timeout);
}
