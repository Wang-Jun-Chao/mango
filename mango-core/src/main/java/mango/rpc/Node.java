package mango.rpc;

import mango.common.URL;

/**
 */
public interface Node {

    void init();

    void destroy();

    boolean isAvailable();

    String desc();

    URL getUrl();
}
