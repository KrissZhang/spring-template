package com.self.security.entity;

import lombok.Data;

@Data
public class User {

    private Integer id;

    private String userName;

    private String password;

    private String realName;

    private Integer deleteFlag;

}
