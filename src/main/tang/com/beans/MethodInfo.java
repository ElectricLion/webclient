package com.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author:tanghui
 * @Date:2018/8/30 18:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {
    /**
     * 请求的url
     */
    private String url;
    /**
     * 请求的方式
     */
    private HttpMethod method;
    /**
     * 请求的参数
     */
    private Map<String, Object> params;
    /**
     * 请求体的内容
     */
    private Mono body;
    /**
     * 请求体的类型
     */
    private Class<?> bodyElementType;
    /**
     * 返回的是flux还是mono
     */
    private boolean returnFlux;
    /**
     * 返回的是flux还是mono
     */
    private Class<?> returnElementType;

}
