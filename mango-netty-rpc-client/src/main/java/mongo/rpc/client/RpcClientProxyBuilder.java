package mongo.rpc.client;

import mongo.rpc.aop.RpcInvokeHook;

import java.lang.reflect.Proxy;

/**
 * RpcClientProxyBuilder是用于产生代理对象的工厂，可生成同步或异步方式的代理对象。
 * Author: 王俊超
 * Date: 2018-02-04 16:19
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClientProxyBuilder {

    /**
     * 创建代理对象的创建器
     */
    public static <T> ProxyBuilder<T> create(Class<T> targetClass) {
        return new ProxyBuilder<T>(targetClass);
    }

    public static class ProxyBuilder<T> {
        private Class<T> clazz;
        private RpcClient rpcClient;

        private long timeoutMills = 0;
        private RpcInvokeHook rpcInvokeHook = null;
        private String host;
        private int port;

        private ProxyBuilder(Class<T> clazz) {
            this.clazz = clazz;
        }

        /**
         * 以毫秒为单位进行超时，设置成0表示不超时，等待结果，此方式只工作在同步模式下，默认是0
         * timeout time in mills. Set to 0 means no timeout and keep waiting for
         * result. Only works in synchronous way. (default 0)
         */
        public ProxyBuilder<T> timeout(long timeoutMills) {
            this.timeoutMills = timeoutMills;
            if (timeoutMills < 0) {
                throw new IllegalArgumentException("timeoutMills can not be minus!");
            }

            return this;
        }

        /**
         * 设置RPC调勾子，当代理对象调用一个方法时，会调用此勾子
         * set the RpcInvokeHook which will be invoke when the proxy object invoke
         * a method. (default null)
         */
        public ProxyBuilder<T> hook(RpcInvokeHook hook) {
            this.rpcInvokeHook = hook;
            return this;
        }

        /**
         * 设置PPC服务器的地址和端口，注意：方法只是设置值，并不会立即连接。连接会在build()或者
         * buildAsyncProxy()方法后建立
         * set the IP address and port of the RpcServer. Note that this method will
         * only set the value but do not connect immediately. Connection will be done in
         * build() or buildAsyncProxy().
         */
        public ProxyBuilder<T> connect(String host, int port) {
            this.host = host;
            this.port = port;
            return this;
        }

        /**
         * 创建一个同步代理。在同步方式下，线程会被阻塞只到获取对方法返回的结果或者超时
         * build the synchronous proxy.In synchronous way, Thread will be blocked until
         * get the result or timeout.
         */
        @SuppressWarnings("unchecked")
        public T build() {
            rpcClient = new RpcClient(timeoutMills, rpcInvokeHook, host, port);
            rpcClient.connect();
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, rpcClient);
        }

        /**
         * 创建一个异步代理。在异步方式下，会立即返回一个RpcFuture对象
         * build the asynchronous proxy.In asynchronous way, a RpcFuture will be
         * return immediately.
         */
        public RpcClientAsyncProxy buildAsyncProxy() {
            rpcClient = new RpcClient(timeoutMills, rpcInvokeHook, host, port);
            rpcClient.connect();
            return new RpcClientAsyncProxy(rpcClient);
        }
    }
}
