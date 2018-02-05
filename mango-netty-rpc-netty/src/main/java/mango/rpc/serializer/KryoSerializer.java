package mango.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * <pre>
 * 实际负责序列化和反序列化
 * serialize()将一个对象通过kryo序列化并写入ByteBuf中，注意到在头部预留了4个字节用于写入长度信息。
 * deserialize()将ByteBuf中的内容反序列化还原出传输的对象。其中序列化和反序列化均用到了kryo对象，
 * 该对象是从KryoHolder中通过get()拿到的。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:33
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class KryoSerializer {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    /**
     * serialize()将一个对象通过kryo序列化并写入ByteBuf中，注意到在头部预留了4个字节用于写入长度信息
     *
     * @param object
     * @param byteBuf
     */
    public static void serialize(Object object, ByteBuf byteBuf) {
        Kryo kryo = KryoHolder.get();
        int startIdx = byteBuf.writerIndex();
        ByteBufOutputStream byteOutputStream = new ByteBufOutputStream(byteBuf);
        try {
            byteOutputStream.write(LENGTH_PLACEHOLDER);
            Output output = new Output(1024 * 4, -1);
            output.setOutputStream(byteOutputStream);
            kryo.writeClassAndObject(output, object);

            output.flush();
            output.close();

            int endIdx = byteBuf.writerIndex();

            byteBuf.setInt(startIdx, endIdx - startIdx - 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deserialize()将ByteBuf中的内容反序列化还原出传输的对象。
     *
     * @param byteBuf
     * @return
     */
    public static Object deserialize(ByteBuf byteBuf) {
        if (byteBuf == null) {
            return null;
        }

        Input input = new Input(new ByteBufInputStream(byteBuf));
        Kryo kryo = KryoHolder.get();
        return kryo.readClassAndObject(input);
    }
}
