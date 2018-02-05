package mango.rpc.serializer;


import com.esotericsoftware.kryo.Serializer;
import de.javakaffee.kryoserializers.*;
import mango.rpc.context.RpcRequest;
import mango.rpc.context.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <pre>
 * 主要完成的功能是给大量类类型注册其对应的Serializer。setRegistrationRequired()设置是否只能序列化已注册的类，
 * 此处必须设置为false，因为RPC请求和回应中都可能包含用户自定义的类，这些类显然是不可能在kryo中注册过的。
 * setReferences()若设置成false在序列化Exception时似乎有问题，此处维持打开（默认也是打开）。注意到给
 * RpcRequest.class和RpcResponse.class分别注册了对应的Serializer为RpcRequestSerializer和RpcResponseSerializer。
 * 这是由于kryo对未注册的类序列化后的格式是包含类的全类名，导致序列化后的字节序列很长，故应该实现一个自定义的Serializer
 * 用于已知类型的序列化和反序列化缩短序列化后的字节序列。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:38
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class KryoReflectionFactory extends KryoReflectionFactorySupport {
    public KryoReflectionFactory() {
        setRegistrationRequired(false);
        setReferences(true);
        register(RpcRequest.class, new RpcRequestSerializer());
        register(RpcResponse.class, new RpcResponseSerializer());
        register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        register(Pattern.class, new RegexSerializer());
        register(BitSet.class, new BitSetSerializer());
        register(URI.class, new URISerializer());
        register(UUID.class, new UUIDSerializer());
        register(GregorianCalendar.class, new GregorianCalendarSerializer());
        register(InvocationHandler.class, new JdkProxySerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(this);
        SynchronizedCollectionsSerializer.registerSerializers(this);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Serializer<?> getDefaultSerializer(Class clazz) {
        if (EnumSet.class.isAssignableFrom(clazz)) {
            return new EnumSetSerializer();
        }

        if (EnumMap.class.isAssignableFrom(clazz)) {
            return new EnumMapSerializer();
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            return new CopyForIterateCollectionSerializer();
        }

        if (Map.class.isAssignableFrom(clazz)) {
            return new CopyForIterateMapSerializer();
        }

        if (Date.class.isAssignableFrom(clazz)) {
            return new DateSerializer(clazz);
        }

        if (SubListSerializers.ArrayListSubListSerializer.canSerialize(clazz)
                || SubListSerializers.JavaUtilSubListSerializer.canSerialize(clazz)) {
            return SubListSerializers.createFor(clazz);
        }

        return super.getDefaultSerializer(clazz);
    }
}
