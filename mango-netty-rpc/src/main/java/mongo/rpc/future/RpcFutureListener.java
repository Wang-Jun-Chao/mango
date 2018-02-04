package mongo.rpc.future;

/**

 * Author: 王俊超
 * Date: 2018-02-04 16:20
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public interface RpcFutureListener {
    public void onResult(Object result);
    public void onException(Throwable throwable);
}
