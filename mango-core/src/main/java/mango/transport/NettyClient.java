package mango.transport;

import mango.core.Request;
import mango.core.Response;
import mango.core.ResponseFuture;
import mango.exception.TransportException;

/**
 * Netty客户端
 */
public interface NettyClient extends Endpoint {

    /**
     * 同步方法调用
     *
     * @param request
     * @return
     * @throws InterruptedException
     * @throws TransportException
     */
    Response invokeSync(final Request request) throws InterruptedException, TransportException;

    /**
     * 异步方法调用
     *
     * @param request
     * @return
     * @throws InterruptedException
     * @throws TransportException
     */
    ResponseFuture invokeAsync(final Request request) throws InterruptedException, TransportException;

    /**
     * TODO ?
     *
     * @param request
     * @throws InterruptedException
     * @throws TransportException
     */
    void invokeOneway(final Request request) throws InterruptedException, TransportException;

}
