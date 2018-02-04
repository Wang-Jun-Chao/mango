package mango.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import mango.rpc.context.RpcResponse;

/**
 * <pre>
 *
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:43
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcResponseSerializer extends Serializer<RpcResponse> {
    @Override
    public void write(Kryo kryo, Output output, RpcResponse object) {
        output.writeInt(object.getId());
        output.writeBoolean(object.isInvokeSuccess());
        if (object.isInvokeSuccess()) {
            kryo.writeClassAndObject(output, object.getResult());
        } else {
            kryo.writeClassAndObject(output, object.getThrowable());
        }
    }

    @Override
    public RpcResponse read(Kryo kryo, Input input, Class<RpcResponse> type) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setId(input.readInt());
        rpcResponse.setInvokeSuccess(input.readBoolean());
        if (rpcResponse.isInvokeSuccess()) {
            rpcResponse.setResult(kryo.readClassAndObject(input));
        } else {
            rpcResponse.setThrowable((Throwable) kryo.readClassAndObject(input));
        }

        return rpcResponse;
    }
}
