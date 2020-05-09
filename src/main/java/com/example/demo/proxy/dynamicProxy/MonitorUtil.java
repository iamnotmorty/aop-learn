package com.example.demo.proxy.dynamicProxy;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 16:56
 * Description: No Description
 */
public class MonitorUtil {

    private static ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void start() {
        tl.set(System.currentTimeMillis());
    }

    public static void destroy() {
        tl.remove();
    }

    /**
     * 结束时打印耗时
     * @param methodName 方法名
     */
    public static void finish(String methodName) {
        long finishTime = System.currentTimeMillis();
        System.out.println(methodName + "方法执行耗时" + (finishTime - tl.get()) + "ms");
        destroy();
    }
}
