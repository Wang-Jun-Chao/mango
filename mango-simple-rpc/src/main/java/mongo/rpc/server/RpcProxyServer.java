package mongo.rpc.server;

import mongo.rpc.service.api.IHello;
import mongo.rpc.service.impl.HelloServiceImpl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 代理服务器，用于从执行代理客户端发送过来的请求
 * Author: 王俊超
 * Date: 2018-02-04 10:10
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */
public class RpcProxyServer {
    private IHello hello = new HelloServiceImpl();

    /**
     * 启动代理服务器
     *
     * @param port
     */
    public void publisherServer(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                // 执行代理客户端发送过来的请求，并且将请求返回
                try (Socket socket = ss.accept()) {
                    try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                        String method = ois.readUTF();
                        Object[] objs = (Object[]) ois.readObject();
                        Class<?>[] types = new Class[objs.length];
                        for (int i = 0; i < types.length; i++) {
                            types[i] = objs[i].getClass();
                        }
                        Method m = HelloServiceImpl.class.getMethod(method, types);
                        Object obj = m.invoke(hello, objs);

                        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                            oos.writeObject(obj);
                            oos.flush();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}