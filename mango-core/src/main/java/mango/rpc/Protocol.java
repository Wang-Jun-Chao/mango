package mango.rpc;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.util.Constants;

/**
 * 协议类
 *
 * @author Ricky Fung
 */
@SPI(value = Constants.FRAMEWORK_NAME)
public interface Protocol {

    /**
     * 获取协议上的对应的引用
     *
     * @param clz
     * @param url
     * @param serviceUrl
     * @param <T>
     * @return
     */
    <T> Reference<T> refer(Class<T> clz, URL url, URL serviceUrl);

    /**
     *
     * @param provider
     * @param url
     * @param <T>
     * @return
     */
    <T> Exporter<T> export(Provider<T> provider, URL url);

    void destroy();
}
