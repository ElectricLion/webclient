package com.interfaces;

import com.beans.MethodInfo;
import com.beans.ServerInfo;

/**
 * @Author:tanghui
 * @Date:2018/8/30 19:06
 */
public interface RestHandler {
    /**
     * 初始化服务器信息
     *
     * @param serverInfo
     */
    void init(ServerInfo serverInfo);

    /**
     * 调用rest 返回接口
     *
     * @return
     * @param methodInfo
     */
    Object invokeRest(MethodInfo methodInfo);
}
