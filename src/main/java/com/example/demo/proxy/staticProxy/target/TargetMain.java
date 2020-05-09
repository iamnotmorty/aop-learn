package com.example.demo.proxy.staticProxy.target;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 11:51
 * Description: No Description
 */
public class TargetMain {
    public static void main(String[] args) {
        executeMethod();
    }

    public static void executeMethod() {
        Target target =  new TargetImpl();
        TargetProxy targetProxy = new TargetProxy(target);
        targetProxy.doSomething();
    }
}
