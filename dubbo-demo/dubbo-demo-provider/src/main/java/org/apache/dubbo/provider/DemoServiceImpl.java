package org.apache.dubbo.provider;

import org.apache.dubbo.demo.DemoService;

public class DemoServiceImpl implements DemoService{
    public String sayHello(String name) {
        return "hello " + name;
    }
}
