package mongo.rpc.service.impl;

import mongo.rpc.service.api.IHello;

/**
 * Author: 王俊超
 * Date: 2018-02-04 10:09
 * Blog: http://blog.csdn.net/derrantcm
 * Github: https://github.com/wang-jun-chao
 * All Rights Reserved !!!
 */

public class HelloServiceImpl implements IHello {
    public String sayHello(String info) {
        String result = "hello : " + info;
        System.out.println(result);
        return result;
    }
}