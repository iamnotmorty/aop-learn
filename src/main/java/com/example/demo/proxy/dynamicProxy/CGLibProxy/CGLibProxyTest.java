package com.example.demo.proxy.dynamicProxy.CGLibProxy;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 17:34
 * Description: No Description
 */
public class CGLibProxyTest {
    public static void main(String[] args) {
        CGLibMethodInterceptor cgLibMethodInterceptor = new CGLibMethodInterceptor();
        Dog dog = (Dog) cgLibMethodInterceptor.cglibProxyGenerate(Dog.class);
        System.out.println(dog.call());
    }
}
