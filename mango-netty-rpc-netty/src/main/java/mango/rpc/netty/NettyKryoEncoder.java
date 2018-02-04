package mango.rpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import mango.rpc.serializer.KryoSerializer;

/**
 * <pre>
 * 用于将发送的对象序列化为字节序列，是一个ChannelOutboundHandler。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 21:05
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        KryoSerializer.serialize(msg, out);
    }
}
