package com.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:tanghui
 * @Date:2018/8/30 18:09
 */
@RestController
@Slf4j
public class TestController {
    @Autowired
    IUserApi userApi; //直接注入定义的接口

    @GetMapping("/")
    public void test() {
        //测试信息获取
        //  userApi.getAllUser();
        // userApi.getUser("1");
        //userApi.deleteUserById("1");
        //    userApi.createUser(Mono.just(User.builder().age(20).id("1").name("youth").build()));
        //直接调用，实现调用Rest接口的效果
        userApi.getAllUser().subscribe(s -> System.out.println(s), throwable -> System.out.println(throwable.getMessage()));
        userApi.deleteUserById("1").subscribe(s -> System.out.println(s), e -> System.out.println(e.getMessage()));

    }

}
