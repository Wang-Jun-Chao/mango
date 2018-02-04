package mongo.rpc;

import mongo.rpc.aop.RpcInvokeHook;
import mongo.rpc.client.RpcClientProxyBuilder;
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
public class SynchronizationTest {

    public static void main(String[] args) {
        RpcInvokeHook hook = new RpcInvokeHook() {
            public void beforeInvoke(String method, Object[] args) {
                System.out.println("before invoke " + method);
            }

            public void afterInvoke(String method, Object[] args) {
                System.out.println("after invoke " + method);
            }
        };

        TestInterface testInterface
                = RpcClientProxyBuilder.create(TestInterface.class)
                .timeout(0)
                .hook(hook)
                .connect("127.0.0.1", 3721)
                .build();

        System.out.println("invoke result = " + testInterface.testMethod01("hello world"));
    }
}
