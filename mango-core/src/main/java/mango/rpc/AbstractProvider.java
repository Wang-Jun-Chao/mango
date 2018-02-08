package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;

import java.lang.reflect.Method;

/**
 * 抽象提供者
 */
public abstract class AbstractProvider<T> implements Provider<T> {
    /**
     * 提供服务的接口
     */
    protected Class<T> clz;
    /**
     * 服务提供者提供服务对应的URL
     */
    protected URL url;
    /**
     * 服务是否可用
     */
    protected boolean available = false;

    public AbstractProvider(URL url, Class<T> clz) {
        this.url = url;
        this.clz = clz;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response call(Request request) {
        Response response = invoke(request);

        return response;
    }

    @Override
    public void init() {
        available = true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void destroy() {
        available = false;
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getName() + "] url=" + url;
    }

    protected abstract Response invoke(Request request);

    /**
     * 获取所请求有方法对象
     *
     * @param request
     * @return
     */
    protected Method lookup(Request request) {
        try {
            return clz.getMethod(request.getMethodName(), request.getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
