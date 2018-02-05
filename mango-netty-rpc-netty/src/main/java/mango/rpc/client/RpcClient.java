package mango.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.context.RpcRequest;
import mango.rpc.future.RpcFuture;
import mango.rpc.netty.NettyKryoDecoder;
import mango.rpc.netty.NettyKryoEncoder;
import mango.rpc.test.TestInterface;
import mango.rpc.utils.InfoPrinter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * RpcClient是实现了InvocationHandler的类，同步方式下的代理对象调用任何方法都会变为调用
 * RpcClient的invoke()方法。因此，我们就能在invoke()方法中获得调用的方法和参数，然后通过
 * Netty将方法名和参数传递至服务器即可。异步方式下最终会直接调用RpcClient的call()方法，返
 * 回一个RpcFuture对象。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 16:32
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClient implements InvocationHandler {

    private long timeoutMills = 0;
    private RpcInvokeHook rpcInvokeHook = null;
    private String host;
    private int port;

    private RpcClientResponseHandler rpcClientResponseHandler;
    private AtomicInteger invokeIdGenerator = new AtomicInteger(0);
    private Bootstrap bootstrap;

    /**
     * channel that connected with the server
     */
    private Channel channel;
    private RpcClientChannelInactiveListener rpcClientChannelInactiveListener;
    private TestInterface testInterface;

    public RpcClient() {
    }

    public RpcClient(long timeoutMills, RpcInvokeHook rpcInvokeHook, String host, int port, int threads) {
        this.timeoutMills = timeoutMills;
        this.rpcInvokeHook = rpcInvokeHook;
        this.host = host;
        this.port = port;

        rpcClientResponseHandler = new RpcClientResponseHandler(threads);
        rpcClientChannelInactiveListener = new RpcClientChannelInactiveListener() {
            public void onInactive() {
                InfoPrinter.println("connection with server is closed.");
                InfoPrinter.println("try to reconnect to the server.");
                channel = null;
                do {
                    channel = tryConnect();
                }
                while (channel == null);
            }
        };
    }

    public void connect() {
        bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new NettyKryoDecoder(),
                                    new RpcClientDispatchHandler(rpcClientResponseHandler, rpcClientChannelInactiveListener),
                                    new NettyKryoEncoder());
                        }
                    });
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            do {
                channel = tryConnect();
            }
            while (channel == null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Channel tryConnect() {
        try {
            InfoPrinter.println("Try to connect to [" + host + ":" + port + "].");
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                InfoPrinter.println("Connect to [" + host + ":" + port + "] successed.");
                return future.channel();
            } else {
                InfoPrinter.println("Connect to [" + host + ":" + port + "] failed.");
                InfoPrinter.println("Try to reconnect in 10s.");
                Thread.sleep(10000);
                return null;
            }
        } catch (Exception exception) {
            InfoPrinter.println("Connect to [" + host + ":" + port + "] failed.");
            InfoPrinter.println("Try to reconnect in 10 seconds.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcFuture rpcFuture = call(method.getName(), args);
        Object result;

        if (timeoutMills == 0) {
            result = rpcFuture.get();
        } else {
            result = rpcFuture.get(timeoutMills);
        }

        if (rpcInvokeHook != null) {
            rpcInvokeHook.afterInvoke(method.getName(), args);
        }

        return result;
    }

    public RpcFuture call(String methodName, Object[] args) {

        if (rpcInvokeHook != null) {
            rpcInvokeHook.beforeInvoke(methodName, args);
        }

        RpcFuture rpcFuture = new RpcFuture();
        int id = invokeIdGenerator.addAndGet(1);
        rpcClientResponseHandler.register(id, rpcFuture);

        RpcRequest rpcRequest = new RpcRequest(id, methodName, args);
        if (channel != null) {
            channel.writeAndFlush(rpcRequest);
        } else {
            return null;
        }

        return rpcFuture;
    }

}
