package com.webclient;

import lombok.*;

/**
 * @Author:tanghui
 * @Date:2018/8/30 18:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String id;
    private String name;
    private int age;


}
