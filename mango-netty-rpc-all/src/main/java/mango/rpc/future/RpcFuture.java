package mango.rpc.future;

import mango.rpc.exception.RpcTimeoutException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RpcFuture {
    private final static int STATE_AWAIT = 0;
    private final static int STATE_SUCCESS = 1;
    private final static int STATE_EXCEPTION = 2;

    private CountDownLatch countDownLatch;
    private Object result;
    private Throwable throwable;
    private volatile int state;
    private RpcFutureListener rpcFutureListener = null;

    public RpcFuture() {
        countDownLatch = new CountDownLatch(1);
        state = STATE_AWAIT;
    }

    public Object get() throws Throwable {
        countDownLatch.await();
        return getResult0();
    }

    public Object get(long timeout) throws Throwable {
        boolean awaitSuccess;
        awaitSuccess = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);

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
            //should not run to here!
            throw new RuntimeException("RpcFuture Exception!");
        }
    }

    /**
     * get result successfully
     *
     * @param result
     */
    public synchronized void setResult(Object result) {
        if (state != STATE_AWAIT) {
            throw new IllegalStateException("can not set result to a RpcFuture instance " +
                    "which has already get result or throwable!");
        }

        this.result = result;
        state = STATE_SUCCESS;

        if (rpcFutureListener != null) {
            rpcFutureListener.onResult(result);
        }

        countDownLatch.countDown();
    }

    /**
     * exception occur when invoke
     *
     * @param throwable
     */
    public synchronized void setThrowable(Throwable throwable) {
        if (state != STATE_AWAIT) {
            throw new IllegalStateException("can not set throwable to a RpcFuture instance " +
                    "which has already get result or throwable!");
        }

        this.throwable = throwable;
        state = STATE_EXCEPTION;

        if (rpcFutureListener != null) {
            rpcFutureListener.onException(throwable);
        }

        countDownLatch.countDown();
    }

    public boolean isDone() {
        return state != STATE_AWAIT;
    }

    public synchronized void setRpcFutureListener(RpcFutureListener rpcFutureListener) {
        if (state != STATE_AWAIT) {
            throw new RuntimeException("unable to set listener to a RpcFuture which is done.");
        }

        this.rpcFutureListener = rpcFutureListener;
    }
}
