package mango.rpc.context;

import io.netty.channel.Channel;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-05 07:54
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcRequestWrapper {
    private final RpcRequest rpcRequest;
    private final Channel channel;

    public RpcRequestWrapper(RpcRequest rpcRequest, Channel channel) {
        this.rpcRequest = rpcRequest;
        this.channel = channel;
    }

    public int getId() {
        return rpcRequest.getId();
    }

    public String getMethodName() {
        return rpcRequest.getMethodName();
    }

    public Object[] getArgs() {
        return rpcRequest.getArgs();
    }

    public Channel getChannel() {
        return channel;
    }
}
