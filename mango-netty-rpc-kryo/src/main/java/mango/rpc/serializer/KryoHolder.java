package mango.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;

/**
 * <pre>
 * 于kryo对象是线程不安全的，当有多个netty的channel同时连接时，各channel是可能工作在不同的线程上的
 * （netty中一个IO线程可以对应多个channel，而一个channel只能对应一个线程，详细可以参考netty线程模型），
 * 若共用同一个kryo对象会出现并发问题，因此用ThreadLocal在每个线程保留一个各自的kryo对象，保证不会大量
 * 创建kryo对象的同时避免了并发问题。最终用于序列化和反序列化的kryo对象是通过new KryoReflectionFactory()创建的。
 * </pre>
 * Author: 王俊超
 * Date: 2018-02-04 20:35
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */

public class KryoHolder {
    private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            return new KryoReflectionFactory();
        }
    };

    public static Kryo get() {
        return threadLocalKryo.get();
    }
}

