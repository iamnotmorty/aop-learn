package com.example.demo.proxy.staticProxy.target;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 11:49
 * Description: No Description
 */
public class TargetProxy implements Target {
    private Target baseObject;

    public TargetProxy(Target baseObject) {
        this.baseObject = baseObject;
    }

    @Override
    public void doSomething(){
        System.out.println("before method");
        baseObject.doSomething();
        System.out.println("after method");
    }

    @Override
    public void doSomething1() {

    }

    @Override
    public void doSomething2() {

    }

    @Override
    public String doSomething3() {
        return null;
    }
}
