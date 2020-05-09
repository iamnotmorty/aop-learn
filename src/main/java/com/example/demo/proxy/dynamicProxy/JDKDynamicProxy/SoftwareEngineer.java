package com.example.demo.proxy.dynamicProxy.JDKDynamicProxy;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 16:46
 * Description: No Description
 */
public class SoftwareEngineer implements Person {

    private String name;

    public SoftwareEngineer(String name) {
        this.name = name;
    }

    @Override
    public void goWorking(String name, String dst) {
        System.out.println("name ="+name+" ， 去 "+dst +" 工作");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
