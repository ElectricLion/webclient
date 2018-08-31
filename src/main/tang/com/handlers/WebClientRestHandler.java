package com.handlers;

import com.beans.MethodInfo;
import com.beans.ServerInfo;
import com.exception.NotFoundException;
import com.interfaces.RestHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @Author:tanghui
 * @Date:2018/8/30 19:45
 */
public class WebClientRestHandler implements RestHandler {

    private WebClient client;
    /**
     * 初始化webclient
     *
     * @param serverInfo
     */
    @Override
    public void init(ServerInfo serverInfo) {
        this.client = WebClient.create(serverInfo.getUrl());
    }

    /**
     * 处理rest 请求
     *
     * @param methodInfo
     * @return
     */
    @Override
    public Object invokeRest(MethodInfo methodInfo) {
        Object result = null;
        WebClient.RequestBodySpec requestBodySpec = this.client
                .method(methodInfo.getMethod())
                .uri(methodInfo.getUrl(), methodInfo.getParams())
                .accept(MediaType.APPLICATION_JSON);
        //判断是否有响应体
        WebClient.ResponseSpec retrieve;
        if (methodInfo.getBody() != null) {
            retrieve = requestBodySpec.body(methodInfo.getBody(), methodInfo.getBodyElementType()).retrieve();
        } else {
            retrieve = requestBodySpec.retrieve();
        }
        //处理异常
        retrieve.onStatus(httpStatus -> httpStatus.value() == 404, clientResponse -> Mono.just(new NotFoundException("NOT FOUND")));
        //判断返回的是Flux还Mono
        if (methodInfo.isReturnFlux()) {
            result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
        } else if (!methodInfo.isReturnFlux()) {
            result = retrieve.bodyToMono(methodInfo.getReturnElementType());
        }
        return result;
    }
}
