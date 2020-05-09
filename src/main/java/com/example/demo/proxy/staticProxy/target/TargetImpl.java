package com.example.demo.proxy.staticProxy.target;

import com.example.demo.proxy.staticProxy.target.Target;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 11:48
 * Description: No Description
 */
public class TargetImpl implements Target {
    @Override
    public void doSomething() {
        System.out.println("target do something!!!");
    }

    private String text = "target";

    @Override
    public void doSomething1(){
        System.out.println("doSomething1-" + text);
    }

    @Override
    public void doSomething2(){
        System.out.println("doSomething2-" + text);
    }

    @Override
    public String doSomething3(){
        System.out.println("doSomething3-" + text);
        String result = "doSomething3-" + text;
        return result;
    }
}
