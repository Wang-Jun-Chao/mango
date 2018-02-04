package mongo.rpc;

import mongo.rpc.aop.RpcInvokeHook;
import mongo.rpc.client.RpcClientAsyncProxy;
import mongo.rpc.client.RpcClientProxyBuilder;
import mongo.rpc.future.RpcFuture;
import mongo.rpc.future.RpcFutureListener;
import mongo.rpc.test.TestInterface;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 18:04
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class AsynchronizationTest {

    public static void main(String[] args) {

        RpcInvokeHook hook = new RpcInvokeHook() {
            public void beforeInvoke(String method, Object[] args) {
                System.out.println("before invoke " + method);
            }

            public void afterInvoke(String method, Object[] args) {
                System.out.println("after invoke " + method);
            }
        };

        RpcClientAsyncProxy rpcClientAsyncProxy = RpcClientProxyBuilder.create(TestInterface.class)
                .timeout(0)
                .hook(hook)
                .connect("127.0.0.1", 3721)
                .buildAsyncProxy();
        RpcFuture rpcFuture = rpcClientAsyncProxy.call("testMethod01", "Hello World");
        rpcFuture.setRpcFutureListener(new RpcFutureListener() {
            public void onResult(Object result) {
                System.out.println("RpcFutureListener result = " + result.toString());
            }

            public void onException(Throwable throwable) {
                System.out.println("RpcFutureListener onException");
            }
        });

        System.out.println("RpcFuture isDone = " + rpcFuture.isDone());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("RpcFuture isDone = " + rpcFuture.isDone());
        try {
            System.out.println("result = " + rpcFuture.get());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
