package mango.cluster.ha;

import mango.cluster.HaStrategy;
import mango.cluster.LoadBalance;
import mango.core.Request;
import mango.core.Response;
import mango.rpc.Reference;

public class FailfastHaStrategy<T> implements HaStrategy<T> {

    @Override
    public Response call(Request request, LoadBalance<T> loadBalance) {
        Reference<T> reference = loadBalance.select(request);
        return reference.call(request);
    }
}
