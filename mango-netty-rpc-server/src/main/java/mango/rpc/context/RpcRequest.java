package mango.rpc.context;

/**
 * <pre>
 * RpcRequest表示了一个RPC调用请求。id用于区分多次不同的调用，methodName为请求调用的方法名，args为参数。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 19:47
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcRequest {
    private int id;
    private String methodName;
    private Object[] args;

    public RpcRequest() {
    }

    public RpcRequest(int id, String methodName, Object[] args) {
        this.id = id;
        this.methodName = methodName;
        this.args = args;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
