package mango.rpc;

import mango.common.URL;

/**
 * 抽象暴露者
 */
public abstract class AbstractExporter<T> implements Exporter<T> {
    /**
     * 提供者
     */
    protected Provider<T> provider;
    /**
     * TODO 是服务URL ？
     */
    protected URL url;

    public AbstractExporter(Provider<T> provider, URL url) {
        this.url = url;
        this.provider = provider;
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getName() + "] url=" + url;
    }

    @Override
    public Provider<T> getProvider() {
        return provider;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
