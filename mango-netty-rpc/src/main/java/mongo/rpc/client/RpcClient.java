package mongo.rpc.client;

import mongo.rpc.aop.RpcInvokeHook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Author: 王俊超
 * Date: 2018-02-04 16:32
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClient implements InvocationHandler {

    private long timeoutMills = 0;
    private RpcInvokeHook rpcInvokeHook = null;
    private String host;
    private int port;

    public RpcClient() {
    }

    public RpcClient(long timeoutMills, RpcInvokeHook rpcInvokeHook, String host, int port) {

    }

    public void connect() {

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    public Object call(String methodName, Object[] args) {
    }
}
