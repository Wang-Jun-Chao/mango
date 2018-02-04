package mango.cluster;

import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.core.extension.Scope;

/**
 * 策略接口
 */
@SPI(scope = Scope.PROTOTYPE)
public interface HaStrategy<T> {
    /**
     *
     * @param request
     * @param loadBalance
     * @return
     */
    Response call(Request request, LoadBalance loadBalance);
}
