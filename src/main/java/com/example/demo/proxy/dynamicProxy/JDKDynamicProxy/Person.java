package com.example.demo.proxy.dynamicProxy.JDKDynamicProxy;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/9
 * Time: 16:44
 * Description: No Description
 */
public interface Person {
    /**
     *
     * @param name
     * @param dst
     */
    void goWorking(String name, String dst);
    /**
     * 获取名称
     * @return
     */
    String getName( );
    /**
     * 设置名称
     * @param name
     */
    void  setName(String name);
}
