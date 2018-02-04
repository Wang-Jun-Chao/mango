package mango.rpc.server;

import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.context.RpcRequest;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;

/**
 * <pre>
 * RpcServerRequestHandleRunnable不断地从请求队列requestQueue中取出方法调用请求RpcRequest，
 * 用serviceProvider调用请求的方法并向客户端返回调用结果。由于现在还未加入网络部分，向客户端返
 * 回结果暂时先用打印输出代替。在方法实际调用的前后，钩子Hook的回调得到了执行。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:01
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServerRequestHandleRunnable implements Runnable {

    private Class<?> interfaceClass;
    private Object serviceProvider;
    private RpcInvokeHook rpcInvokeHook;
    private BlockingQueue<RpcRequest> requestQueue;

    public RpcServerRequestHandleRunnable(Class<?> interfaceClass, Object serviceProvider,
                                          RpcInvokeHook rpcInvokeHook, BlockingQueue<RpcRequest> requestQueue) {
        this.interfaceClass = interfaceClass;
        this.serviceProvider = serviceProvider;
        this.rpcInvokeHook = rpcInvokeHook;
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                RpcRequest rpcRequest = requestQueue.take();
                String methodName = rpcRequest.getMethodName();
                Object[] args = rpcRequest.getArgs();

                int parameterCount = args.length;
                Method method = null;

                if (parameterCount > 0) {
                    Class<?>[] parameterTypes = new Class[parameterCount];
                    for (int i = 0; i < parameterCount; i++) {
                        parameterTypes[i] = args[i].getClass();
                    }
                    method = interfaceClass.getMethod(methodName, parameterTypes);
                } else {
                    method = interfaceClass.getMethod(methodName);
                }

                if (rpcInvokeHook != null) {
                    rpcInvokeHook.beforeInvoke(methodName, args);
                }

                Object result = method.invoke(serviceProvider, args);
                System.out.println("Send response id = " + rpcRequest.getId() + " result = " + result
                        + " back to client. " + Thread.currentThread());

                if (rpcInvokeHook != null) {
                    rpcInvokeHook.afterInvoke(methodName, args);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
