package mango.rpc.exception;

public class RpcTimeoutException extends RuntimeException {
    private static final long serialVersionUID = -3399060930740626516L;

    public RpcTimeoutException() {
        super("time out when calling a Rpc Invoke!");
    }
}
