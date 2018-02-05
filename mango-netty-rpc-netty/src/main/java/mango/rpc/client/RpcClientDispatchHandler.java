package mango.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import mango.rpc.context.RpcResponse;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 21:16
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClientDispatchHandler extends ChannelInboundHandlerAdapter {
    private RpcClientResponseHandler rpcClientResponseHandler;
    private RpcClientChannelInactiveListener rpcClientChannelInactiveListener;

    public RpcClientDispatchHandler(RpcClientResponseHandler rpcClientResponseHandler,
                                    RpcClientChannelInactiveListener rpcClientChannelInactiveListener) {
        this.rpcClientResponseHandler = rpcClientResponseHandler;
        this.rpcClientChannelInactiveListener = rpcClientChannelInactiveListener;
    }

    /**
     * channelRead()在收到某个对象（由NettyKryoDecoder解码还原得到的对象）后，强转为RpcResponse并调用
     * RpcClientResponseHandler的addResponse()方法添加收到的RpcResponse
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) msg;
        rpcClientResponseHandler.addResponse(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (rpcClientChannelInactiveListener != null) {
            rpcClientChannelInactiveListener.onInactive();
        }
    }
}
