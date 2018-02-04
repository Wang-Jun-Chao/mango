package mango.rpc.serializer;


import de.javakaffee.kryoserializers.*;
import mango.rpc.context.RpcRequest;
import mango.rpc.context.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <pre>
 *
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
}
