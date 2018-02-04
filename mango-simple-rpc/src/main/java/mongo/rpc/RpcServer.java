package mongo.rpc;

import mongo.rpc.server.RpcProxyServer;

/**
 * Author: 王俊超
 * Date: 2018-02-04 10:13
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcServer {
    //发布服务
    public static void main(String[] args) {
        RpcProxyServer server = new RpcProxyServer();
        server.publisherServer(9999);
    }
}
