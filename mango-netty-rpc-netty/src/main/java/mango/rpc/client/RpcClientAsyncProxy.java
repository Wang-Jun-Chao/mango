package mango.rpc.client;

import mongo.rpc.future.RpcFuture;

/**
 * <pre>
 * 同步方式下，直接在build()返回的JDK动态代理对象上面进行方法调用即可。然而在异步方式下，由于方法的调用会统一
 * 返回RpcFuture对象，因此需要用到RpcClientAsyncProxy。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 16:33
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClientAsyncProxy {

    private RpcClient rpcClient;

    public RpcClientAsyncProxy() {
    }

    public RpcClientAsyncProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public RpcFuture call(String methodName, Object... args) {
        return (RpcFuture) rpcClient.call(methodName, args);
    }
}
