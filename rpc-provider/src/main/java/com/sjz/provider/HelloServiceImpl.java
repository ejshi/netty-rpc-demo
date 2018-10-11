package com.sjz.provider;


import com.sjz.api.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String message) {
        return "hello message " + message;
    }

    @Override
    public void hi(String message) {
        System.out.println("hi 我起作用了！！！");
    }
}
