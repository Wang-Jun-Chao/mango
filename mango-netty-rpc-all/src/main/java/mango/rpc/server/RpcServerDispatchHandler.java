package mango.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mango.rpc.context.RpcRequest;
import mango.rpc.context.RpcRequestWrapper;

public class RpcServerDispatchHandler extends ChannelInboundHandlerAdapter {
    private RpcServerRequestHandler rpcServerRequestHandler;

    public RpcServerDispatchHandler(RpcServerRequestHandler rpcServerRequestHandler) {
        this.rpcServerRequestHandler = rpcServerRequestHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        RpcRequestWrapper rpcRequestWrapper = new RpcRequestWrapper(rpcRequest, ctx.channel());

        rpcServerRequestHandler.addRequest(rpcRequestWrapper);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
