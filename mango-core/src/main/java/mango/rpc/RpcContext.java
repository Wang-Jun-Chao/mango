package mango.rpc;

import mango.core.Request;
import mango.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC上下文对象
 */
public class RpcContext {
    private static final ThreadLocal<RpcContext> localContext = new ThreadLocal<RpcContext>() {
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    /**
     * 用于记录RPC上下文附加属性
     */
    private Map<Object, Object> attribute = new HashMap<>();
    private Request request;
    private Response response;
    private Long requestId;

    public static RpcContext getContext() {
        return localContext.get();
    }

    /**
     * 创建RPC上下文对象，并且将对象放入到
     *
     * @param request
     * @return
     */
    public static RpcContext init(Request request) {
        RpcContext context = new RpcContext();
        if (request != null) {
            context.setRequest(request);
            context.setRequestId(request.getRequestId());
        }
        localContext.set(context);
        return context;
    }

    public static void destroy() {
        localContext.remove();
    }

    public void putAttribute(Object key, Object value) {
        attribute.put(key, value);
    }

    public Object getAttribute(Object key) {
        return attribute.get(key);
    }

    public void removeAttribute(Object key) {
        attribute.remove(key);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Long getRequestId() {
        return requestId;
    }

    void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
