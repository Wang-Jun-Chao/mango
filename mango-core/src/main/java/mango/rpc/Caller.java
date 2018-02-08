package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 * 调用者类
 */
public interface Caller<T> extends Node {

    /**
     * 调用者需要调用的接口类型
     *
     * @return
     */
    Class<T> getInterface();

    /**
     * 方法调用
     *
     * @param request
     * @return
     */
    Response call(Request request);

}
