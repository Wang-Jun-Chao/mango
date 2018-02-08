package mango.rpc;

import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcBizException;
import mango.exception.RpcFrameworkException;
import mango.util.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 消息路由对象
 */
public class MessageRouter implements MessageHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 记录提供者
     */
    private ConcurrentMap<String, Provider<?>> providers = new ConcurrentHashMap<>();

    public MessageRouter() {
    }

    public MessageRouter(Provider<?> provider) {
        addProvider(provider);
    }

    /**
     * 处理请求
     *
     * @param request
     * @return
     */
    @Override
    public Response handle(Request request) {

        // 获取服务Key
        String serviceKey = FrameworkUtils.getServiceKey(request);

        // 如果服务提供者不存在就抛出异常
        Provider<?> provider = providers.get(serviceKey);

        if (provider == null) {
            logger.error(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey);
            RpcFrameworkException exception = new RpcFrameworkException(this.getClass().getSimpleName()
                    + " handler Error: provider not exist serviceKey=" + serviceKey);

            DefaultResponse response = new DefaultResponse();
            response.setException(exception);
            return response;
        }

        // 请求处理
        return call(request, provider);
    }

    /**
     * 请求处理
     *
     * @param request
     * @param provider
     * @return
     */
    protected Response call(Request request, Provider<?> provider) {
        try {
            // 调用提供者进行请求处理
            return provider.call(request);
        } catch (Exception e) {
            // 如果有异常就封闭异常并且返回响应
            DefaultResponse response = new DefaultResponse();
            response.setException(new RpcBizException("provider call process error", e));
            return response;
        }
    }

    /**
     * 添加提供者，如果提供者已经存在就抛出异常
     *
     * @param provider
     */
    public synchronized void addProvider(Provider<?> provider) {
        String serviceKey = FrameworkUtils.getServiceKey(provider.getUrl());
        if (providers.containsKey(serviceKey)) {
            throw new RpcFrameworkException("provider alread exist: " + serviceKey);
        }
        providers.put(serviceKey, provider);
        logger.info("RequestRouter addProvider: url=" + provider.getUrl());
    }

    /**
     * 删除提供者
     *
     * @param provider
     */
    public synchronized void removeProvider(Provider<?> provider) {
        String serviceKey = FrameworkUtils.getServiceKey(provider.getUrl());
        providers.remove(serviceKey);
        logger.info("RequestRouter removeProvider: url=" + provider.getUrl());
    }
}
