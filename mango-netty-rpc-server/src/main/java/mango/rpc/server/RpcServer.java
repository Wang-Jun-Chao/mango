package mango.rpc.server;

import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.context.RpcRequest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * RpcServer只提供了start()和stop()方法用于启动和停止RPC服务。由于启动和停止要涉及网络部分，现在先用打印输出代替。
 * start()方法中还模拟了收到RpcRequest的情况，用于当前无网络连接的情况下测试。RpcServer的构造方法中创建了一个
 * RpcServerRequestHandler，专门用于处理RpcRequest。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 19:52
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServer {
    private Class<?> interfaceClass;
    private Object serviceProvider;

    private int port;
    private int threads;
    private RpcInvokeHook rpcInvokeHook;

    private RpcServerRequestHandler rpcServerRequestHandler;

    public RpcServer() {
    }

    public RpcServer(Class<?> interfaceClass, Object serviceProvider, int port, int threads,
                     RpcInvokeHook rpcInvokeHook) {
        this.interfaceClass = interfaceClass;
        this.serviceProvider = serviceProvider;
        this.port = port;
        this.threads = threads;
        this.rpcInvokeHook = rpcInvokeHook;

        rpcServerRequestHandler = new RpcServerRequestHandler(interfaceClass, serviceProvider, threads, rpcInvokeHook);
        rpcServerRequestHandler.start();
    }

    public void start() {
        System.out.println("bind port:" + port + " success!");

        // 模拟接收RPC请求
        //simulation for receive RpcRequest
        AtomicInteger idGenerator = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            rpcServerRequestHandler.addRequest(new RpcRequest(idGenerator.addAndGet(1), "testMethod01", new Object[]{"Hello World"}));
        }
    }

    public void stop() {
        System.out.println("server stop success!");
    }
}
