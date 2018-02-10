package mango.registry.zookeeper;

import mango.common.URL;
import mango.exception.RpcFrameworkException;
import mango.registry.AbstractRegistry;
import mango.registry.NotifyListener;
import mango.util.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * zookeeper注册中心
 */
public class ZookeeperRegistry extends AbstractRegistry {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 客户端锁
     */
    private final ReentrantLock clientLock = new ReentrantLock();
    /**
     * 服务端锁
     */
    private final ReentrantLock serverLock = new ReentrantLock();
    /**
     * 服务监听器
     */
    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, IZkChildListener>> serviceListeners = new ConcurrentHashMap<>();

    /**
     * zookeeper客户端
     */
    private ZkClient zkClient;

    /**
     * 创建Zookeeper客户端
     *
     * @param url
     * @param zkClient
     */
    public ZookeeperRegistry(URL url, ZkClient zkClient) {
        super(url);
        this.zkClient = zkClient;
        /**
         * zookeeper状态监听器，只作状态通知
         */
        IZkStateListener zkStateListener = new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                // do nothing
            }

            @Override
            public void handleNewSession() throws Exception {
                logger.info("zkRegistry get new session notify.");

            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

            }
        };
        // 订阅状态
        this.zkClient.subscribeStateChanges(zkStateListener);
    }

    /**
     * 注册RUL
     *
     * @param url
     */
    @Override
    protected void doRegister(URL url) {
        try {
            serverLock.lock();
            // 防止旧节点未正常注销
            removeNode(url, ZkNodeType.SERVER);
            createNode(url, ZkNodeType.SERVER);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to register %s to zookeeper(%s), cause: %s",
                    url, getUrl(), e.getMessage()), e);
        } finally {
            serverLock.unlock();
        }
    }

    /**
     * 取消注册
     *
     * @param url
     */
    @Override
    protected void doUnregister(URL url) {
        try {
            serverLock.lock();
            removeNode(url, ZkNodeType.SERVER);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to unregister %s to zookeeper(%s), cause: %s",
                    url, getUrl(), e.getMessage()), e);
        } finally {
            serverLock.unlock();
        }
    }

    /**
     * 进行服务注册
     *
     * @param url
     * @param listener
     */
    @Override
    protected void doSubscribe(final URL url, final NotifyListener listener) {
        try {
            clientLock.lock();

            // 获取结点变化的监听器集合
            ConcurrentMap<NotifyListener, IZkChildListener> childChangeListeners = serviceListeners.get(url);
            if (childChangeListeners == null) {
                serviceListeners.putIfAbsent(url, new ConcurrentHashMap<NotifyListener, IZkChildListener>());
                childChangeListeners = serviceListeners.get(url);
            }

            // 获取zookeeper孩子监听器
            IZkChildListener zkChildListener = childChangeListeners.get(listener);
            // 如果zookeeper孩子监听器不存在就创建
            if (zkChildListener == null) {
                childChangeListeners.putIfAbsent(listener, new IZkChildListener() {
                    @Override
                    public void handleChildChange(String parentPath, List<String> currentChilds) {
                        // 处理孩子结点变化事件
                        listener.notify(getUrl(), childrenNodeToUrls(parentPath, currentChilds));
                        logger.info(String.format("[ZookeeperRegistry] service list change: path=%s, currentChilds=%s",
                                parentPath, currentChilds.toString()));
                    }
                });
                zkChildListener = childChangeListeners.get(listener);
            }

            // 防止旧节点未正常注销
            removeNode(url, ZkNodeType.CLIENT);
            createNode(url, ZkNodeType.CLIENT);

            String serverTypePath = ZkUtils.toNodeTypePath(url, ZkNodeType.SERVER);
            zkClient.subscribeChildChanges(serverTypePath, zkChildListener);
            logger.info(String.format("[ZookeeperRegistry] subscribe service: path=%s, info=%s",
                    ZkUtils.toNodePath(url, ZkNodeType.SERVER), url.toFullUri()));
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to subscribe %s to zookeeper(%s), cause: %s",
                    url, getUrl(), e.getMessage()), e);
        } finally {
            clientLock.unlock();
        }
    }

    /**
     * 取消订阅
     *
     * @param url
     * @param listener
     */
    @Override
    protected void doUnsubscribe(URL url, NotifyListener listener) {
        try {
            clientLock.lock();
            // 获取指定url下的所有子孩子监听器
            Map<NotifyListener, IZkChildListener> childChangeListeners = serviceListeners.get(url);
            if (childChangeListeners != null) {
                IZkChildListener zkChildListener = childChangeListeners.get(listener);
                if (zkChildListener != null) {
                    // 取消孩子结点的状态改变事件
                    zkClient.unsubscribeChildChanges(ZkUtils.toNodeTypePath(url, ZkNodeType.CLIENT), zkChildListener);
                    // 在孩子监听器集合中删除指定监听器
                    childChangeListeners.remove(listener);
                }
            }
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to unsubscribe service %s to zookeeper(%s), cause: %s",
                    url, getUrl(), e.getMessage()), e);
        } finally {
            clientLock.unlock();
        }
    }

    /**
     * 服务发现
     *
     * @param url
     * @return
     */
    @Override
    protected List<URL> doDiscover(URL url) {
        return discoverService(url);
    }

    /**
     * 创建结点
     *
     * @param url
     * @param nodeType
     */
    private void createNode(URL url, ZkNodeType nodeType) {
        // 如果结点类型不存在，就创建对应的持久类型的结点
        String nodeTypePath = ZkUtils.toNodeTypePath(url, nodeType);
        if (!zkClient.exists(nodeTypePath)) {
            zkClient.createPersistent(nodeTypePath, true);
        }

        // 创建临时结点
        zkClient.createEphemeral(ZkUtils.toNodePath(url, nodeType), url.toFullUri());
    }

    /**
     * 删除结点
     *
     * @param url
     * @param nodeType
     */
    private void removeNode(URL url, ZkNodeType nodeType) {
        String nodePath = ZkUtils.toNodePath(url, nodeType);
        if (zkClient.exists(nodePath)) {
            zkClient.delete(nodePath);
        }
    }

    /**
     * 服务发现
     *
     * @param url
     * @return
     */
    private List<URL> discoverService(URL url) {
        try {
            // 获取父结点路径
            String parentPath = ZkUtils.toNodeTypePath(url, ZkNodeType.SERVER);
            List<String> children = new ArrayList<>();
            if (zkClient.exists(parentPath)) {
                // 获取父结点下的所有孩子信息
                children = zkClient.getChildren(parentPath);
            }
            // 将孩子结点转换成URL集合对象返回
            return childrenNodeToUrls(parentPath, children);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format("Failed to discover service %s from zookeeper(%s), cause: %s",
                    url, getUrl(), e.getMessage()), e);
        }
    }

    /**
     * 获取孩子结点的完整路径，并且转为URL对象集合返回
     *
     * @param parentPath
     * @param children
     * @return
     */
    private List<URL> childrenNodeToUrls(String parentPath, List<String> children) {
        List<URL> urls = new ArrayList<>();
        if (children != null) {
            for (String node : children) {
                String nodePath = parentPath + Constants.PATH_SEPARATOR + node;
                // TODO  ?
                String data = zkClient.readData(nodePath, true);
                try {
                    URL url = URL.parse(data);
                    urls.add(url);
                } catch (Exception e) {
                    logger.warn(String.format("Found malformed urls from ZookeeperRegistry, path=%s", nodePath), e);
                }
            }
        }
        return urls;
    }

    /**
     * 关闭zookeeper客户端
     */
    @Override
    public void close() {
        this.zkClient.close();
    }
}
