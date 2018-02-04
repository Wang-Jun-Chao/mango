package mongo.rpc.future;

import mongo.rpc.exception.RpcTimeoutException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 异步方式下会立即返回一个Future，可通过isDone()判断结果是否已返回，通过get()获取返回结果，
 * 也可通过setRpcFutureListener()设置结果返回时的回调接口。
 *
 * RpcFuture内部使用了CountDownLatch，初始值为1。只有在调用setResult()方法（正常返回了远程调用结果）
 * 或调用setThrowable()方法（调用过程中有异常发生）后，CountDownLatch才会减到0。在此之前，若设置了
 * Listener的将获得回调，若对RpcFuture对象调用get()方法将阻塞。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 16:19
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcFuture {
    public final static int STATE_AWAIT = 0;
    public final static int STATE_SUCCESS = 1;
    public final static int STATE_EXCEPTION = 2;

    private CountDownLatch countDownLatch;
    private Object result;
    private Throwable throwable;
    private int state;
    private RpcFutureListener rpcFutureListener = null;

    public RpcFuture() {
        countDownLatch = new CountDownLatch(1);
        state = STATE_AWAIT;
    }

    public Object get() throws Throwable {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return getResult0();
    }

    public Object get(long timeout) throws Throwable {
        boolean awaitSuccess = true;
        try {
            awaitSuccess = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!awaitSuccess) {
            throw new RpcTimeoutException();
        }

        return getResult0();
    }

    private Object getResult0() throws Throwable {
        if (state == STATE_SUCCESS) {
            return result;
        } else if (state == STATE_EXCEPTION) {
            throw throwable;
        } else {
            // 方法不应该运行到此处
            // should not run to here!
            throw new RuntimeException("RpcFuture Exception!");
        }
    }

    public void setResult(Object result) {
        this.result = result;
        state = STATE_SUCCESS;

        if (rpcFutureListener != null)
            rpcFutureListener.onResult(result);

        countDownLatch.countDown();
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        state = STATE_EXCEPTION;

        if (rpcFutureListener != null)
            rpcFutureListener.onException(throwable);

        countDownLatch.countDown();
    }

    public boolean isDone() {
        return state != STATE_AWAIT;
    }

    public void setRpcFutureListener(RpcFutureListener rpcFutureListener) {
        this.rpcFutureListener = rpcFutureListener;
    }
}
