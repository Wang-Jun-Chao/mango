package mango.rpc.server;

import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.context.RpcRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <pre>
 * 专门用于处理RpcRequest的类
 * 在RpcServerRequestHandler的构造方法中，创建了1个大小为threads的线程池，并让其运行了threads个
 * RpcServerRequestHandleRunnable。每个RpcServerRequestHandleRunnable持有相同的服务接口
 * interfaceClass表示服务端提供哪些服务，相同的服务提供对象serviceProvider供实际方法调用，相同的
 * 请求队列requestQueue用于取出收到的方法调用请求。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 19:55
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServerRequestHandler {
    private Class<?> interfaceClass;
    private Object serviceProvider;
    private RpcInvokeHook rpcInvokeHook;

    private int threads;

    private ExecutorService threadPool;
    private BlockingQueue<RpcRequest> requestQueue = new LinkedBlockingQueue<RpcRequest>();


    public RpcServerRequestHandler() {
    }

    public RpcServerRequestHandler(Class<?> interfaceClass, Object serviceProvider, int threads,
                                   RpcInvokeHook rpcInvokeHook) {
        this.interfaceClass = interfaceClass;
        this.serviceProvider = serviceProvider;
        this.threads = threads;
        this.rpcInvokeHook = rpcInvokeHook;
    }

    public void start() {
        threadPool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            threadPool.execute(new RpcServerRequestHandleRunnable(interfaceClass,
                    serviceProvider, rpcInvokeHook, requestQueue));
        }
    }

    public void addRequest(RpcRequest rpcRequest) {
        try {
            requestQueue.put(rpcRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
