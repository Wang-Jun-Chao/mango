package mango.serialization.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mango.codec.Serializer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * protostuff序列化器
 */
public class ProtostuffSerializer implements Serializer {

    private static final LoadingCache<Class<?>, Schema<?>> schemas = CacheBuilder.newBuilder()
            .build(new CacheLoader<Class<?>, Schema<?>>() {
                @Override
                public Schema<?> load(Class<?> cls) throws Exception {
                    return RuntimeSchema.createFrom(cls);
                }
            });

    private static Schema getSchema(Class<?> cls) throws IOException {
        try {
            return schemas.get(cls);
        } catch (ExecutionException e) {
            throw new IOException("create protostuff schema error", e);
        }
    }

    /**
     * 序列化
     *
     * @param msg
     * @return
     * @throws IOException
     */
    @Override
    public byte[] serialize(Object msg) throws IOException {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(msg.getClass());
            return ProtostuffIOUtil.toByteArray(msg, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 返回序列化
     *
     * @param buf
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] buf, Class<T> type) throws IOException {
        Schema<T> schema = getSchema(type);
        T msg = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(buf, msg, schema);
        return (T) msg;
    }
}
