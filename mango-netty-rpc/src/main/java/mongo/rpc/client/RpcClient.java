package mongo.rpc.client;

import mongo.rpc.aop.RpcInvokeHook;
import mongo.rpc.future.RpcFuture;
import mongo.rpc.test.TestInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <pre>
 * RpcClient是实现了InvocationHandler的类，同步方式下的代理对象调用任何方法都会变为调用
 * RpcClient的invoke()方法。因此，我们就能在invoke()方法中获得调用的方法和参数，然后通过
 * Netty将方法名和参数传递至服务器即可。异步方式下最终会直接调用RpcClient的call()方法，返
 * 回一个RpcFuture对象。
 * </pre>
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

    TestInterface testInterface;

    public RpcClient() {
    }

    public RpcClient(long timeoutMills, RpcInvokeHook rpcInvokeHook, String host, int port) {
        this.timeoutMills = timeoutMills;
        this.rpcInvokeHook = rpcInvokeHook;
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("connect to " + host + ":" + port + " success!");

        testInterface = new TestInterface() {
            public String testMethod01(String string) {
                return string.toUpperCase();
            }
        };
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcFuture rpcFuture = call(method.getName(), args);
        Object result;

        if (timeoutMills == 0) {
            result = rpcFuture.get();
        } else {
            result = rpcFuture.get(timeoutMills);
        }

        if (rpcInvokeHook != null) {
            rpcInvokeHook.afterInvoke(method.getName(), args);
        }

        return result;
    }

    public RpcFuture call(String methodName, Object[] args) {
        if (rpcInvokeHook != null) {
            rpcInvokeHook.beforeInvoke(methodName, args);
        }

        System.out.print("invoke method = " + methodName + " args =");
        for (Object argObject : args) {
            System.out.print(" " + argObject.toString());
        }
        System.out.println("");

        // 模拟远程调用
        // simulation for remote invoke
        RpcFuture rpcFuture = new RpcFuture();
        TestThread testThread = new TestThread(rpcFuture, methodName, args);
        testThread.start();

        return rpcFuture;
    }

    class TestThread extends Thread {
        String methodName;
        Object[] args;
        RpcFuture rpcFuture;

        public TestThread(RpcFuture rpcFuture, String methodName, Object[] args) {
            this.rpcFuture = rpcFuture;
            this.methodName = methodName;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                int parameterCount = args.length;
                Method method;
                if (parameterCount > 0) {
                    Class<?>[] parameterTypes = new Class[args.length];
                    for (int i = 0; i < parameterCount; i++) {
                        parameterTypes[i] = args[i].getClass();
                    }
                    method = testInterface.getClass().getMethod(methodName, parameterTypes);
                } else {
                    method = testInterface.getClass().getMethod(methodName);
                }

                rpcFuture.setResult(method.invoke(testInterface, args));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
