package com.webclient;

import com.interfaces.ProxyGreator;
import com.proxys.JDKProxyGreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebclientApplication.class, args);
    }

    @Bean
    public ProxyGreator jdkProxyGreator() {
        return new JDKProxyGreator();
    }

    @Bean
    FactoryBean<IUserApi> iUserApi(ProxyGreator proxyGreator) {
        return new FactoryBean<IUserApi>() {
            @Override
            public IUserApi getObject() throws Exception {
                return (IUserApi) proxyGreator.createProxy(this.getObjectType());
            }

            @Override
            public Class<?> getObjectType() {
                return IUserApi.class;
            }
        };

    }
}
