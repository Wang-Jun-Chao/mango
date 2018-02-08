package mango.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.*;
import mango.exception.RpcFrameworkException;
import mango.exception.TransportException;
import mango.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Netty客户端实现
 */
public class NettyClientImpl extends AbstractClient {

    /**
     * 记录异步调用的结果包装对象
     */
    private final ConcurrentMap<Long, ResponseFuture> responseFutureMap = new ConcurrentHashMap<>(256);
    /**
     * 客户端NIO线程组
     */
    private EventLoopGroup group = new NioEventLoopGroup();
    /**
     * 客户端启动类
     */
    private Bootstrap bootstrap = new Bootstrap();
    /**
     * 定时任务，用于处理超时的请求
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 是否初始化的标记
     */
    private volatile boolean initializing;

    /**
     * 通道包装类
     */
    private volatile ChannelWrapper channelWrapper;

    public NettyClientImpl(URL url) {
        super(url);
        // 获取远程主机地址
        this.remoteAddress = new InetSocketAddress(url.getHost(), url.getPort());
        // 获取超时时间
        this.timeout = url.getIntParameter(URLParam.requestTimeout.getName(), URLParam.requestTimeout.getIntValue());

        // 创建定时任务线程池
        this.scheduledExecutorService = Executors.newScheduledThreadPool(5,
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "future")));

        // 设置定时任务
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanRpcFutureTable();
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 打开客户端连接
     *
     * @return
     */
    @Override
    public synchronized boolean open() {

        if (initializing) {
            logger.warn("NettyClient is initializing: url=" + url);
            return true;
        }

        // 标记已经初始化，防止并发调用过程重复初始化
        initializing = true;

        // 通道已经可用了，说明是已经初始化好了，可以直接使用了
        if (state.isAvailable()) {
            logger.warn("NettyClient has initialized: url=" + url);
            return true;
        }

        // 最大响应包限制
        final int maxContentLength = url.getIntParameter(URLParam.maxContentLength.getName(),
                URLParam.maxContentLength.getIntValue());

        bootstrap.group(group).channel(NioSocketChannel.class)
                // 采用延迟发送
                .option(ChannelOption.TCP_NODELAY, true)
                // 采用TPC长连接
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 设置接收到发送缓冲区的大小
                .option(ChannelOption.SO_RCVBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .option(ChannelOption.SO_SNDBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new NettyDecoder(codec, url, maxContentLength, Constants.HEADER_SIZE, 4), //
                                new NettyEncoder(codec, url), //
                                new NettyClientHandler());
                    }
                });

        try {
            // 发起异步连楼操作
            ChannelFuture channelFuture = bootstrap.connect(this.remoteAddress).sync();
            // 包装通道
            this.channelWrapper = new ChannelWrapper(channelFuture);
        } catch (InterruptedException e) {
            logger.error(String.format("NettyClient connect to address:%s failure", this.remoteAddress), e);
            throw new RpcFrameworkException(String.format("NettyClient connect to address:%s failure"), e);
        }

        // 更新通道状态，到这一步通道才真的是可用的
        state = ChannelState.AVAILABLE;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return state.isAvailable();
    }

    @Override
    public boolean isClosed() {
        return state.isClosed();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    /**
     * 同步调用，直接到RPC方法结果返回否则一直阻塞
     *
     * @param request
     * @return
     * @throws InterruptedException
     * @throws TransportException
     */
    @Override
    public Response invokeSync(final Request request) throws InterruptedException, TransportException {
        Channel channel = getChannel();
        if (channel != null && channel.isActive()) {
            final ResponseFuture<Response> rpcFuture = new DefaultResponseFuture<>(timeout);
            this.responseFutureMap.put(request.getRequestId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getRequestId());

                    } else {
                        logger.info("send failure, request id:{}", request.getRequestId());
                        responseFutureMap.remove(request.getRequestId());
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });
            return rpcFuture.get();
        } else {
            throw new TransportException("channel not active. request id:" + request.getRequestId());
        }
    }

    @Override
    public ResponseFuture invokeAsync(final Request request) throws InterruptedException, TransportException {
        Channel channel = getChannel();
        if (channel != null && channel.isActive()) {

            final ResponseFuture<Response> rpcFuture = new DefaultResponseFuture<>(timeout);
            this.responseFutureMap.put(request.getRequestId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getRequestId());
                    }
                }
            });
            return rpcFuture;
        } else {
            throw new TransportException("channel not active. request id:" + request.getRequestId());
        }
    }

    @Override
    public void invokeOneway(final Request request) throws InterruptedException, TransportException {
        Channel channel = getChannel();
        if (channel != null && channel.isActive()) {
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        logger.info("send success, request id:{}", request.getRequestId());
                    } else {
                        logger.info("send failure, request id:{}", request.getRequestId());
                    }
                }
            });
        } else {
            throw new TransportException("channel not active. request id:" + request.getRequestId());
        }
    }

    @Override
    public void close() {
        close(0);
    }

    /**
     * 关闭客户端连接，如果已经关闭就直接返回，没有关闭就开始关闭线程池
     * 关闭客户端NIO线程组，设置客户端状态为已经关闭
     *
     * @param timeout
     */
    @Override
    public synchronized void close(int timeout) {

        if (state.isClosed()) {
            logger.info("NettyClient close fail: already close, url={}", url.getUri());
            return;
        }

        try {
            this.scheduledExecutorService.shutdown();
            this.group.shutdownGracefully();

            state = ChannelState.CLOSED;
        } catch (Exception e) {
            logger.error("NettyClient close Error: url=" + url.getUri(), e);
        }

    }

    /**
     * 获取通道对象，如果不存在通道或者通道不可用，就重新创建新的通道，并且返回
     *
     * @return
     * @throws InterruptedException
     */
    private Channel getChannel() throws InterruptedException {

        if (this.channelWrapper != null && this.channelWrapper.isActive()) {
            return this.channelWrapper.getChannel();
        }

        synchronized (this) {
            // 发起异步连接操作
            ChannelFuture channelFuture = bootstrap.connect(this.remoteAddress).sync();
            this.channelWrapper = new ChannelWrapper(channelFuture);
        }

        return this.channelWrapper.getChannel();
    }

    /**
     * 定时清理超时Future
     **/
    private void scanRpcFutureTable() {

        long currentTime = System.currentTimeMillis();
        logger.info("scan timeout RpcFuture, currentTime:{}", currentTime);

        final List<ResponseFuture> timeoutFutureList = new ArrayList<>();
        Iterator<Map.Entry<Long, ResponseFuture>> it = this.responseFutureMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, ResponseFuture> next = it.next();
            ResponseFuture future = next.getValue();

            if (future.isTimeout()) {  //超时
                it.remove();
                timeoutFutureList.add(future);
            }
        }

        for (ResponseFuture future : timeoutFutureList) {
            //释放资源
        }
    }

    private class NettyClientHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {

            logger.info("client read msg:{}, ", msg);
            if (msg instanceof Response) {
                DefaultResponse response = (DefaultResponse) msg;

                ResponseFuture<Response> rpcFuture = responseFutureMap.get(response.getRequestId());
                if (rpcFuture != null) {
                    responseFutureMap.remove(response.getRequestId());
                    rpcFuture.setResult(response);
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.error("client caught exception", cause);
            ctx.close();
        }
    }
}
