package mango.rpc.context;

/**
 * <pre>
 * RpcResponse中的id对应着该次请求的RpcRequest中的id，isInvokeSuccess表示调用中是否有异常抛出，
 * result和throwable分别表示调用结果和调用过程抛出的异常。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:31
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcResponse {
    private int id;
    private Object result;
    private Throwable throwable;
    private boolean isInvokeSuccess;

    public RpcResponse() {
    }

    public RpcResponse(int id, Object resultOrThrowable, boolean isInvokeSuccess) {
        this.id = id;
        this.isInvokeSuccess = isInvokeSuccess;

        if (isInvokeSuccess) {
            result = resultOrThrowable;
        } else {
            throwable = (Throwable) resultOrThrowable;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isInvokeSuccess() {
        return isInvokeSuccess;
    }

    public void setInvokeSuccess(boolean invokeSuccess) {
        isInvokeSuccess = invokeSuccess;
    }
}
