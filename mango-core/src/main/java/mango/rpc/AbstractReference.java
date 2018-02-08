package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 抽象引用对象
 */
public abstract class AbstractReference<T> implements Reference<T> {
    /**
     * 引用对应的接口类类型
     */
    protected Class<T> clz;
    /**
     * 服务的URL
     */
    protected URL serviceUrl;
    /**
     * 调用计数器
     */
    protected AtomicInteger activeCounter = new AtomicInteger(0);
    private URL url;

    public AbstractReference(Class<T> clz, URL serviceUrl) {
        this.clz = clz;
        this.serviceUrl = serviceUrl;
    }

    public AbstractReference(Class<T> clz, URL url, URL serviceUrl) {
        this.clz = clz;
        this.url = url;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public URL getServiceUrl() {
        return serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    /**
     * 根据请求调用方法
     *
     * @param request
     * @return
     */
    @Override
    public Response call(Request request) {
        if (!isAvailable()) {
            throw new RpcFrameworkException(this.getClass().getName() + " call Error: node is not available, url=" + url.getUri());
        }

        // 调用计数增加
        incrActiveCount(request);
        Response response = null;
        try {
            // 方法调用
            response = doCall(request);
        } finally {
            // 减少调用计数
            decrActiveCount(request, response);
        }

        return response;
    }

    @Override
    public int activeCount() {
        return activeCounter.get();
    }

    protected abstract Response doCall(Request request);

    protected void decrActiveCount(Request request, Response response) {
        activeCounter.decrementAndGet();
    }

    protected void incrActiveCount(Request request) {
        activeCounter.incrementAndGet();
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getName() + "] url=" + url;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
