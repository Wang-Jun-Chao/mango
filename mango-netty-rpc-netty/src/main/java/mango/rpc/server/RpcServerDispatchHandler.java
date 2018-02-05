package mango.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mango.rpc.context.RpcRequest;

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
public class RpcServerDispatchHandler extends ChannelInboundHandlerAdapter {
    private RpcServerRequestHandler rpcServerRequestHandler;

    public RpcServerDispatchHandler(
            RpcServerRequestHandler rpcServerRequestHandler) {
        this.rpcServerRequestHandler = rpcServerRequestHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        RpcRequestWrapper rpcRequestWrapper = new RpcRequestWrapper(rpcRequest, ctx.channel());

        rpcServerRequestHandler.addRequest(rpcRequestWrapper);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

    }
}
