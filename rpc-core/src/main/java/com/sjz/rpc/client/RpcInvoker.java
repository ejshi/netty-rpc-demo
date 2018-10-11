package com.sjz.rpc.client;

import java.lang.reflect.Proxy;

public class RpcInvoker {

    public static <T> T create(Class<T> tClass){

        return (T)Proxy.newProxyInstance(tClass.getClassLoader(),
                new Class<?>[]{tClass}, new RpcInvocationHandler(tClass));
    }
}
