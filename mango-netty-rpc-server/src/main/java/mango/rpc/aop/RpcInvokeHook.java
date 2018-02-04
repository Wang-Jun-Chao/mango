package mango.rpc.aop;

/**
 * RpcClient 或者 RpcServer 调用时才调用，可以用于记录调用时间和调用记录
 * Author: 王俊超
 * Date: 2018-02-04 16:21
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public interface RpcInvokeHook {
    void beforeInvoke(String methodName, Object[] args);

    void afterInvoke(String methodName, Object[] args);
}
