package mango.rpc.server;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.netty.channel.Channel;
import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.context.RpcResponse;

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
    private BlockingQueue<RpcRequestWrapper> requestQueue;
    private RpcRequestWrapper rpcRequestWrapper;

    private MethodAccess methodAccess;
    private String lastMethodName = "";
    private int lastMethodIndex;

    public RpcServerRequestHandleRunnable(Class<?> interfaceClass,
                                          Object serviceProvider, RpcInvokeHook rpcInvokeHook,
                                          BlockingQueue<RpcRequestWrapper> requestQueue) {
        this.interfaceClass = interfaceClass;
        this.serviceProvider = serviceProvider;
        this.rpcInvokeHook = rpcInvokeHook;
        this.requestQueue = requestQueue;

        methodAccess = MethodAccess.get(interfaceClass);
    }

    public void run() {
        while (true) {
            try {
                rpcRequestWrapper = requestQueue.take();

                String methodName = rpcRequestWrapper.getMethodName();
                Object[] args = rpcRequestWrapper.getArgs();

                if (rpcInvokeHook != null)
                    rpcInvokeHook.beforeInvoke(methodName, args);

                Object result = null;
                if (!methodName.equals(lastMethodName)) {
                    lastMethodIndex = methodAccess.getIndex(methodName);
                    lastMethodName = methodName;
                }

                result = methodAccess.invoke(serviceProvider, lastMethodIndex, args);

                Channel channel = rpcRequestWrapper.getChannel();

                int id = rpcRequestWrapper.getId();
                RpcResponse rpcResponse = new RpcResponse(id, result, true);
                channel.writeAndFlush(rpcResponse);

                if (rpcInvokeHook != null)
                    rpcInvokeHook.afterInvoke(methodName, args);
            } catch (Exception e) {
                Channel channel = rpcRequestWrapper.getChannel();
                int id = rpcRequestWrapper.getId();
                RpcResponse rpcResponse = new RpcResponse(id, e, false);
                channel.writeAndFlush(rpcResponse);
            }
        }
    }
}
