package mango.rpc.client;

import mango.rpc.context.RpcResponse;
import mango.rpc.future.RpcFuture;

import java.util.concurrent.*;

public class RpcClientResponseHandler {
    private ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap = new ConcurrentHashMap<>();

    private ExecutorService threadPool;
    private BlockingQueue<RpcResponse> responseQueue = new LinkedBlockingQueue<>();

    public RpcClientResponseHandler(int threads) {
        threadPool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            threadPool.execute(new RpcClientResponseHandleRunnable(invokeIdRpcFutureMap, responseQueue));
        }
    }

    public void register(int id, RpcFuture rpcFuture) {
        invokeIdRpcFutureMap.put(id, rpcFuture);
    }

    public void addResponse(RpcResponse rpcResponse) {
        responseQueue.add(rpcResponse);
    }
}
