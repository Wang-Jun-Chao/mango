package mango.registry;

import mango.common.URL;
import mango.exception.RpcFrameworkException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    /**
     * 获取注册的URL
     */
    private final ConcurrentHashMap<String, Registry> registries = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 获取注册对象，如果没有就创建注册对象，并且记录注册对象然后返回，否则就直接返回记录的注册对象
     *
     * @param url
     * @return
     */
    @Override
    public Registry getRegistry(URL url) {
        String registryUri = getRegistryUri(url);
        try {
            lock.lock();
            Registry registry = registries.get(registryUri);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(url);
            if (registry == null) {
                throw new RpcFrameworkException("Create registry false for url:" + url);
            }
            registries.put(registryUri, registry);
            return registry;
        } catch (Exception e) {
            throw new RpcFrameworkException("Create registry false for url:" + url, e);
        } finally {
            lock.unlock();
        }
    }

    protected String getRegistryUri(URL url) {
        return url.getUri();
    }

    protected abstract Registry createRegistry(URL url);
}
