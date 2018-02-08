package mango.registry;

import mango.common.URL;

import java.util.List;

/**
 * 通过监听服务
 */
public interface NotifyListener {

    /**
     * TODO
     * @param registryUrl
     * @param urls
     */
    void notify(URL registryUrl, List<URL> urls);
}
