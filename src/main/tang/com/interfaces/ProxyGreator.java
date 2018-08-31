package com.interfaces;

/**
 * 创建代理类接口
 *
 * @Author:tanghui
 * @Date:2018/8/30 18:41
 */
public interface ProxyGreator {
    /**
     * 创建代理类的方法
     *
     * @param type
     * @return
     */
    Object createProxy(Class<?> type);
}
