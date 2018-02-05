package mango.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import mango.rpc.aop.RpcInvokeHook;
import mango.rpc.netty.NettyKryoDecoder;
import mango.rpc.netty.NettyKryoEncoder;
import mango.rpc.utils.InfoPrinter;

/**
 * <pre>
 * RpcServer只提供了start()和stop()方法用于启动和停止RPC服务。由于启动和停止要涉及网络部分，现在先用打印输出代替。
 * start()方法中还模拟了收到RpcRequest的情况，用于当前无网络连接的情况下测试。RpcServer的构造方法中创建了一个
 * RpcServerRequestHandler，专门用于处理RpcRequest。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 19:52
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServer {
    private Class<?> interfaceClass;
    private Object serviceProvider;

    private int port;
    private int threads;
    private RpcInvokeHook rpcInvokeHook;

    private RpcServerRequestHandler rpcServerRequestHandler;

    public RpcServer() {
    }

    public RpcServer(Class<?> interfaceClass, Object serviceProvider, int port, int threads,
                     RpcInvokeHook rpcInvokeHook) {
        this.interfaceClass = interfaceClass;
        this.serviceProvider = serviceProvider;
        this.port = port;
        this.threads = threads;
        this.rpcInvokeHook = rpcInvokeHook;

        rpcServerRequestHandler = new RpcServerRequestHandler(interfaceClass, serviceProvider, threads, rpcInvokeHook);
        rpcServerRequestHandler.start();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyKryoDecoder(),
                                    new RpcServerDispatchHandler(rpcServerRequestHandler),
                                    new NettyKryoEncoder());
                        }
                    });
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(port);
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            InfoPrinter.println("RpcServer started.");
            InfoPrinter.println(interfaceClass.getSimpleName() + " in service.");
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        InfoPrinter.println("RpcServer started.");
        InfoPrinter.println(interfaceClass.getSimpleName() + " in service.");
    }

    public void stop() {
        System.out.println("server stop success!");
    }
}
