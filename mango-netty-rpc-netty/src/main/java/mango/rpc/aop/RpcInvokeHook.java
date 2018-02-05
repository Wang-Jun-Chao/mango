package mango.rpc.aop;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-05 07:57
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public interface RpcInvokeHook {
    void beforeInvoke(String methodName, Object[] args);

    void afterInvoke(String methodName, Object[] args);
}
