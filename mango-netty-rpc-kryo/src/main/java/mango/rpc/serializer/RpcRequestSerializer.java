package mango.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import mango.rpc.context.RpcRequest;

/**
 * <pre>
 * write()中按顺序往output中写入id，调用方法名的长度和调用方法名的字节数组，最后是调用方法的参数列表，
 * 由于不知道参数的确切类型，此处调用传进的kryo对象的writeClassAndObject()方法对参数进行序列化。
 * read()中按照相同的顺序读出值并根据这些值构建出一个RpcRequest对象并返回。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:43
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcRequestSerializer extends Serializer<RpcRequest> {
    @Override
    public void write(Kryo kryo, Output output, RpcRequest object) {
        output.writeInt(object.getId());
        output.writeByte(object.getMethodName().length());
        output.write(object.getMethodName().getBytes());
        kryo.writeClassAndObject(output, object.getArgs());
    }

    @Override
    public RpcRequest read(Kryo kryo, Input input, Class<RpcRequest> type) {
        int id = input.readInt();
        byte methodLength = input.readByte();
        byte[] methodBytes = input.readBytes(methodLength);
        String methodName = new String(methodBytes);
        Object[] args = (Object[]) kryo.readClassAndObject(input);

        return new RpcRequest(id, methodName, args);
    }
}
