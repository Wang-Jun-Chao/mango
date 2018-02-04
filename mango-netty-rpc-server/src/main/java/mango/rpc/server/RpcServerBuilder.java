package mango.rpc.server;

import mango.rpc.aop.RpcInvokeHook;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 19:49
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServerBuilder {
    private Class<?> interfaceClass;
    private Object serviceProvider;

    private int port;
    private int threads;
    private RpcInvokeHook rpcInvokeHook;

    /**
     * 创建工场
     *
     * @return
     */
    public static RpcServerBuilder create() {
        return new RpcServerBuilder();
    }

    /**
     * 设置服务接口
     * set the interface to provide service
     *
     * @param interfaceClass
     */
    public RpcServerBuilder serviceInterface(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return this;
    }

    /**
     * 设置供方法调用的实际对象
     * set the real object to provide service
     */
    public RpcServerBuilder serviceProvider(Object serviceProvider) {
        this.serviceProvider = serviceProvider;
        return this;
    }

    /**
     * 设置绑定的端口号
     * set the port to bind
     */
    public RpcServerBuilder bind(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置Handler线程的个数（默认为CPU核数）
     * set the count of threads to handle request from client. (default availableProcessors)
     */
    public RpcServerBuilder threads(int threadCount) {
        this.threads = threadCount;
        return this;
    }

    /**
     * 设置服务端方法回调
     * set the hook of the method invoke in server
     */
    public RpcServerBuilder hook(RpcInvokeHook rpcInvokeHook) {
        this.rpcInvokeHook = rpcInvokeHook;
        return this;
    }

    /**
     * 创建出RpcServer对象
     *
     * @return
     */
    public RpcServer build() {
        if (threads <= 0) {
            threads = Runtime.getRuntime().availableProcessors();
        }
        return new RpcServer(interfaceClass, serviceProvider, port, threads, rpcInvokeHook);
    }
}
