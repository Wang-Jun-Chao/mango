package mongo.rpc;

import mongo.rpc.client.RpcProxyClient;
import mongo.rpc.service.api.IHello;

/**
 * Author: 王俊超
 * Date: 2018-02-04 10:14
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcClient {
    // 调用服务
    public static void main(String[] args) {
        RpcProxyClient rpcClient = new RpcProxyClient();

        IHello hello = rpcClient.proxyClient(IHello.class, "localhost", 9999);
        String s = hello.sayHello("welcome to rpc");
        System.out.println(s);
    }
}
