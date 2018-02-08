package mango.registry;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.core.extension.Scope;

/**
 * 注册工厂
 */
@SPI(scope = Scope.SINGLETON)
public interface RegistryFactory {

    /**
     * 通过URL对象获取注册对象
     *
     * @param url
     * @return
     */
    Registry getRegistry(URL url);
}
