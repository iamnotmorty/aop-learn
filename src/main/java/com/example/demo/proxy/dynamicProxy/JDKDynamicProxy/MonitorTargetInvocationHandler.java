package com.example.demo.proxy.dynamicProxy.JDKDynamicProxy;

import com.example.demo.proxy.dynamicProxy.MonitorUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 16:52
 * Description: No Description
 *
 * MonitorTargetInvocationHandler 类 实现InvocationHandler接口，
 * 这个类中持有一个被代理对象(委托类)的实例target。该类别JDK Proxy类回调
 *
 * InvocationHandler 接口中有一个invoke方法，当一个代理实例的方法被调用时，
 * 代理方法将被编码并分发到 InvocationHandler接口的invoke方法执行。
 */
public class MonitorTargetInvocationHandler<T> implements InvocationHandler {

    T target;

    public MonitorTargetInvocationHandler(T target) {
        this.target = target;
    }

    /**
     * 在
     * @param proxy  代表动态生成的 动态代理 对象实例
     * @param method 代表被调用委托类的接口方法，和生成的代理类实例调用的接口方法是一致的，它对应的Method 实例
     * @param args   代表调用接口方法对应的Object参数数组，如果接口是无参，则为null； 对于原始数据类型返回的他的包装类型。
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 在转调具体目标对象之前，可以执行一些功能处理
        System.out.println("被动态代理类回调执行, 代理类 proxyClass ="+proxy.getClass()+" 方法名: " + method.getName() + "方法. 方法返回类型："+method.getReturnType()
                +" 接口方法入参数组: "+(args ==null ? "null" : Arrays.toString(args)));
        // 代理过程中插入监测方法,计算该方法耗时
        MonitorUtil.start();
        // 由于测试例子方法太简单，执行太快。让线程睡眠一段时间，模拟方法调用时间
        Thread.sleep(100);
        // 调用被代理对象的真实方法
        Object result = method.invoke(target, args);
        MonitorUtil.finish(method.getName());
        return result;
    }

    public static Object proxy(Class<?> interfaceClazz, Object proxy) {
        return Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz},
                new MonitorTargetInvocationHandler<>(proxy));
    }
}
