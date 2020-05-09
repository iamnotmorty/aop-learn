package com.example.demo.proxy.dynamicProxy.JDKDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 17:00
 * Description: No Description
 */
public class JDKDynamicProxyTest {
    public static void main(String[] args) {
        Person person = new SoftwareEngineer("Vincent");
        Person personProxy = (Person) MonitorTargetInvocationHandler.proxy(Person.class, person);
        System.out.println("package = " + personProxy.getClass().getPackage() + " SimpleName = " + personProxy.getClass().getSimpleName() + " name =" + personProxy.getClass().getName() + " CanonicalName = " + "" + personProxy.getClass().getCanonicalName() + " 实现的接口 Interfaces = " + Arrays.toString(personProxy.getClass().getInterfaces()) + " superClass = " + personProxy.getClass().getSuperclass() + " methods =" + Arrays.toString(personProxy.getClass().getMethods()));        // 通过 代理类 执行 委托类的代码逻辑
        personProxy.goWorking(personProxy.getName(), "深圳");
    }
}
