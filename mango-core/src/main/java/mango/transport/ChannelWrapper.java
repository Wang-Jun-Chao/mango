package mango.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通道包装类
 */
public class ChannelWrapper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChannelFuture channelFuture;

    public ChannelWrapper(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    /**
     * 判断通道是否激活
     *
     * @return
     */
    public boolean isActive() {
        return (this.channelFuture.channel() != null && this.channelFuture.channel().isActive());
    }

    /**
     * 判断通道是否可写
     *
     * @return
     */
    public boolean isWritable() {
        return this.channelFuture.channel().isWritable();
    }

    /**
     * 获取通道对象
     *
     * @return
     */
    public Channel getChannel() {
        return this.channelFuture.channel();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    /**
     * 关闭通道
     */
    public void close() {
        getChannel().close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("closeChannel: close the connection to remote address:{}, result: {}",
                        getChannel().remoteAddress(), future.isSuccess());
            }
        });
    }
}
