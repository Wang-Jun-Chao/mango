package mongo.rpc.exception;

/**
 * Author: 王俊超
 * Date: 2018-02-04 16:30
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -4988552322380183571L;

    public RpcTimeoutException() {
        super("time out when calling a Rpc Invoke!");
    }
}
