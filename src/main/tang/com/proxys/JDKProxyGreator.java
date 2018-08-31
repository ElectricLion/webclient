package com.proxys;

import com.beans.MethodInfo;
import com.beans.ServerInfo;
import com.handlers.WebClientRestHandler;
import com.interfaces.ProxyGreator;
import com.interfaces.RestHandler;
import com.webclient.ApiServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author:tanghui
 * @Date:2018/8/30 18:49
 */
@Slf4j
public class JDKProxyGreator implements ProxyGreator {
    @Override
    public Object createProxy(Class<?> type) {
        log.info("createProxy:" + type);
        ServerInfo serverInfo = extractServerInfo(type);
        log.info("serverInfo:" + serverInfo);
        RestHandler restHandler = new WebClientRestHandler();
        restHandler.init(serverInfo);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                //根据方法和参数得到调用信息
                MethodInfo methodInfo = extractMethodInfo(method, objects);
                log.info("methodInfo:" + methodInfo);
                //调用Rest
                Object object = restHandler.invokeRest(methodInfo);
                return object;
            }

            /**
             * 根据方法定义和调用参数得到调用的相关信息
             * @param method
             * @param objects
             * @return
             */
            private MethodInfo extractMethodInfo(Method method, Object[] objects) {
                MethodInfo methodInfo = new MethodInfo();
                extractUrlAndMethod(method, methodInfo);
                extractRequestParamsAndBody(method, objects, methodInfo);
                extractReturnInfo(method, methodInfo);
                return methodInfo;
            }
        });
    }

    /**
     * 提取返回对象信息
     *
     * @param method
     * @param methodInfo
     */
    private void extractReturnInfo(Method method, MethodInfo methodInfo) {
        // 返回是Flux还是Mono
        //instanceof 判断实例是否是某个的子类
        boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
        methodInfo.setReturnFlux(isFlux);
        Class<?> elementType = extractElementType(method.getGenericReturnType());
        methodInfo.setReturnElementType(elementType);
    }

    /**
     * 得到泛型类型的实际类型
     *
     * @param genericReturnType
     * @return
     */
    private Class<?> extractElementType(Type genericReturnType) {
        Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * 得到请求的参数和body 请求体
     *
     * @param method
     * @param objects
     * @param methodInfo
     */
    private void extractRequestParamsAndBody(Method method, Object[] objects, MethodInfo methodInfo) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> params = new LinkedHashMap<>();
        methodInfo.setParams(params);
        for (int i = 0; i < parameters.length; i++) {
            //是否带@PathVariable
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                params.put(pathVariable.value(), objects[i]);
                log.info(objects[i].toString());
            }
            //是否带了RequestBody
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                methodInfo.setBody((Mono<?>) objects[i]);
               methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
            }
        }
    }

    /**
     * 得到url 和请求的方式
     *
     * @param method
     * @param methodInfo
     */
    private void extractUrlAndMethod(Method method, MethodInfo methodInfo) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            //GET
            if (annotation instanceof GetMapping) {
                GetMapping a = (GetMapping) annotation;
                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.GET);
            }
            //Post
            else if (annotation instanceof PostMapping) {
                PostMapping a = (PostMapping) annotation;
                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.POST);
            }
            //DELETE
            else if (annotation instanceof DeleteMapping) {
                DeleteMapping a = (DeleteMapping) annotation;
                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.DELETE);
            }
        }
    }

    /**
     * 提取服务器信息
     *
     * @param type
     * @return
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ServerInfo serverInfo = new ServerInfo();
        ApiServer apiServer = type.getAnnotation(ApiServer.class);  //可以通过反射来推出该类有哪些方法和属性，从而不需要事先知道该类有哪些东西
        serverInfo.setUrl(apiServer.value());
        return serverInfo;
    }
}
