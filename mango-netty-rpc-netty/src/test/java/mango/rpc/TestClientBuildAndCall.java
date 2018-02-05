package mango.rpc;

import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.client.RpcClientProxyBuilder;
import mango.rpc.test.TestInterface;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-05 08:30
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class TestClientBuildAndCall {
    public static void main(String[] args) {
        RpcInvokeHook hook = new RpcInvokeHook() {
            public void beforeInvoke(String method, Object[] args) {
                System.out.println("before invoke in client" + method);
            }

            public void afterInvoke(String method, Object[] args) {
                System.out.println("after invoke in client" + method);
            }
        };

        final TestInterface testInterface
                = RpcClientProxyBuilder.create(TestInterface.class)
                .timeout(0)
                .threads(4)
                .hook(hook)
                .connect("127.0.0.1", 3721)
                .build();

        for (int i = 0; i < 10; i++) {
            System.out.println("invoke result = " + testInterface.testMethod01());
        }
    }
}
