package mango.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求ID生成器
 */
public class RequestIdGenerator {
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public static long getRequestId() {
        return idGenerator.getAndIncrement();
    }
}
