package mango.rpc;

import mango.common.URL;
import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcBizException;
import mango.exception.RpcFrameworkException;

import java.lang.reflect.Method;

/**
 * 默认提供者类
 */
public class DefaultProvider<T> extends AbstractProvider<T> {
    /**
     * 代理接口的具体实现
     */
    protected T proxyImpl;

    public DefaultProvider(T proxyImpl, URL url, Class<T> clz) {
        super(url, clz);
        this.proxyImpl = proxyImpl;
    }

    /**
     * 获取所代理的接口
     *
     * @return
     */
    @Override
    public Class<T> getInterface() {
        return clz;
    }

    /**
     * 根据请求调用实现类的方法
     *
     * @param request
     * @return
     */
    @Override
    public Response invoke(Request request) {

        DefaultResponse response = new DefaultResponse();
        response.setRequestId(request.getRequestId());

        // 查询代理的方法
        Method method = lookup(request);
        if (method == null) {
            RpcFrameworkException exception =
                    new RpcFrameworkException("Service method not exist: " + request.getInterfaceName() + "." + request.getMethodName());

            response.setException(exception);
            return response;
        }
        try {
            // 执行方法调用并且返回结果，如果有异常抛出就将异常封装到响应中并且返回
            method.setAccessible(true);
            Object result = method.invoke(proxyImpl, request.getArguments());
            response.setResult(result);
        } catch (Exception e) {
            response.setException(new RpcBizException("invoke failure", e));
        }
        return response;
    }
}
