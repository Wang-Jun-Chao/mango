package mango.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import mango.rpc.serializer.KryoSerializer;

/**
 * <pre>
 * NettyKryoDecoder用于从接收到的字节序列还原出对象，是一个ChannelInboundHandler。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 21:10
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class NettyKryoDecoder extends LengthFieldBasedFrameDecoder {
    public NettyKryoDecoder() {
        // 最大支持8MB的数据
        super(1048576, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        return KryoSerializer.deserialize(frame);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        // NOTE:
        return buffer.slice(index, length);
    }
}
