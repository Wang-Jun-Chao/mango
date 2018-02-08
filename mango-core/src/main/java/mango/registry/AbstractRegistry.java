package mango.registry;

import com.google.common.base.Preconditions;
import mango.common.URL;
import mango.util.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 抽象注册类
 *
 * @author Ricky Fung
 */
public abstract class AbstractRegistry implements Registry {

    /**
     * 记录已经注册的服务
     */
    protected Set<URL> registeredServiceUrls = new ConcurrentHashSet<URL>();

    /**
     * TODO ?
     */
    private URL registryUrl;

    /**
     * 创建抽象注册对象
     */
    public AbstractRegistry(URL url) {
        this.registryUrl = url.clone0();
    }

    /**
     * 添加注册的URL
     * @param url
     * @throws Exception
     */
    @Override
    public void register(URL url) throws Exception {
        Preconditions.checkNotNull(url);
        doRegister(url.clone0());
        registeredServiceUrls.add(url);
    }

    @Override
    public void unregister(URL url) throws Exception {
        Preconditions.checkNotNull(url);
        doUnregister(url.clone0());
        registeredServiceUrls.remove(url);
    }

    @Override
    public void subscribe(URL url, NotifyListener listener) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(listener);

        URL urlCopy = url.clone0();
        doSubscribe(urlCopy, listener);

        //第一次订阅时主动推一次
//        List<URL> urls = doDiscover(urlCopy);
//        if (urls != null && urls.size() > 0) {
//            listener.notify(url, urls);
//        }
    }

    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(listener);
        doUnsubscribe(url.clone0(), listener);
    }

    @Override
    public List<URL> discover(URL url) throws Exception {
        Preconditions.checkNotNull(url);

        List<URL> results = new ArrayList<>();
        List<URL> urlsDiscovered = doDiscover(url);
        if (urlsDiscovered != null) {
            for (URL u : urlsDiscovered) {
                results.add(u.clone0());
            }
        }
        return results;
    }

    @Override
    public URL getUrl() {
        return registryUrl;
    }

    /**
     * 进行注册
     * @param url
     */
    protected abstract void doRegister(URL url);

    protected abstract void doUnregister(URL url);

    protected abstract void doSubscribe(URL url, NotifyListener listener);

    protected abstract void doUnsubscribe(URL url, NotifyListener listener);

    protected abstract List<URL> doDiscover(URL url);
}
