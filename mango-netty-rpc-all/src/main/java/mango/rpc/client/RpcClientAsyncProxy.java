package mango.rpc.client;

import mango.rpc.exception.RpcMethodNotFoundException;
import mango.rpc.future.RpcFuture;
import mango.rpc.utils.InfoPrinter;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class RpcClientAsyncProxy {
    private RpcClient rpcClient;
    private Set<String> serviceMethodSet = new HashSet<String>();

    public RpcClientAsyncProxy(RpcClient rpcClient, Class<?> interfaceClass) {
        this.rpcClient = rpcClient;
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            serviceMethodSet.add(method.getName());
        }
    }

    public RpcFuture call(String methodName, Object... args) {
        if (!serviceMethodSet.contains(methodName))
            throw new RpcMethodNotFoundException(methodName);

        RpcFuture callResult = rpcClient.call(methodName, args);

        if (callResult != null)
            return callResult;
        else {
            InfoPrinter.println("RpcClient is unavailable when disconnect with the server.");
            return null;
        }
    }
}
