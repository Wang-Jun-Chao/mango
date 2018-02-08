package mango.registry;

import mango.common.URL;

/**
 * 注册服务
 */
public interface RegistryService {

    /**
     * 注册服务
     *
     * @param url
     */
    void register(URL url) throws Exception;

    /**
     * 注销服务
     *
     * @param url
     */
    void unregister(URL url) throws Exception;

}