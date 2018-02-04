package mango.rpc;

import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.server.RpcServer;
import mango.rpc.server.RpcServerBuilder;
import mango.rpc.test.TestInterface;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:12
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class ServerTest {
    public static void main(String[] args) {
        TestInterface testInterface = new TestInterface() {
            public String testMethod01(String string) {
                return string.toUpperCase();
            }
        };

        RpcInvokeHook hook = new RpcInvokeHook() {
            public void beforeInvoke(String methodName, Object[] args) {
                System.out.println("beforeInvoke " + methodName);
            }

            public void afterInvoke(String methodName, Object[] args) {
                System.out.println("afterInvoke " + methodName);
            }
        };

        RpcServer rpcServer = RpcServerBuilder.create()
                .serviceInterface(TestInterface.class)
                .serviceProvider(testInterface)
                .threads(4)
                .hook(hook)
                .bind(3721)
                .build();
        rpcServer.start();

    }
}
