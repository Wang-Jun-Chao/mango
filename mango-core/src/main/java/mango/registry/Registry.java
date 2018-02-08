package mango.registry;

import mango.common.URL;

/**
 * 注册接口
 */
public interface Registry extends RegistryService, DiscoveryService {

    /**
     * 注册的URL
     * @return
     */
    URL getUrl();

    /**
     * 关闭注册对象
     */
    void close();
}
