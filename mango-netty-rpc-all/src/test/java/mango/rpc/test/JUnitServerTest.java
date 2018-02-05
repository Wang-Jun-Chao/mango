package mango.rpc.test;

import mango.rpc.server.RpcServer;
import mango.rpc.server.RpcServerBuilder;
import org.junit.Test;

public class JUnitServerTest {
    public static void main(String[] args) {
        new JUnitServerTest().testServerStart();
    }

    @Test
    public void testServerStart() {
        JUnitTestInterfaceImpl jUnitTestInterfaceImpl = new JUnitTestInterfaceImpl();
        RpcServer rpcServer = RpcServerBuilder.create()
                .serviceInterface(JUnitTestInterface.class)
                .serviceProvider(jUnitTestInterfaceImpl)
                .threads(4)
                .bind(3721)
                .build();
        rpcServer.start();
    }
}
