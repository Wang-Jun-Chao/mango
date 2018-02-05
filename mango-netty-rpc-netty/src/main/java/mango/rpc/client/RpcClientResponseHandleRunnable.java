package mango.rpc.client;

import mango.rpc.context.RpcResponse;
import mango.rpc.future.RpcFuture;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-05 08:22
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClientResponseHandleRunnable implements Runnable {
    private ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap;
    private BlockingQueue<RpcResponse> responseQueue;

    public RpcClientResponseHandleRunnable(ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap,
                                           BlockingQueue<RpcResponse> responseQueue) {
        this.invokeIdRpcFutureMap = invokeIdRpcFutureMap;
        this.responseQueue = responseQueue;
    }

    public void run() {
        while (true) {
            try {
                RpcResponse rpcResponse = responseQueue.take();
                int id = rpcResponse.getId();
                RpcFuture rpcFuture = invokeIdRpcFutureMap.remove(id);

                if (rpcResponse.isInvokeSuccess()) {
                    rpcFuture.setResult(rpcResponse.getResult());
                } else {
                    rpcFuture.setThrowable(rpcResponse.getThrowable());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
