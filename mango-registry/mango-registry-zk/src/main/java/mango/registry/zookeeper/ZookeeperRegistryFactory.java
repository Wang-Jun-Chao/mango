package mango.registry.zookeeper;

import mango.common.URL;
import mango.common.URLParam;
import mango.registry.AbstractRegistryFactory;
import mango.registry.Registry;
import org.I0Itec.zkclient.ZkClient;

/**
 * zookeeper注册工厂
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    /**
     * 创建注册类
     *
     * @param registryUrl
     * @return
     */
    @Override
    protected Registry createRegistry(URL registryUrl) {
        // 获取超时时间
        int timeout = registryUrl.getIntParameter(
                URLParam.registryConnectTimeout.getName(),
                URLParam.registryConnectTimeout.getIntValue());

        // 获取session超时时间
        int sessionTimeout = registryUrl.getIntParameter(
                URLParam.registrySessionTimeout.getName(),
                URLParam.registrySessionTimeout.getIntValue());

        // 创建zookeeper客户端
        ZkClient zkClient = new ZkClient(registryUrl.getParameter(
                URLParam.registryAddress.getName()),
                sessionTimeout,
                timeout);
        return new ZookeeperRegistry(registryUrl, zkClient);
    }
}
