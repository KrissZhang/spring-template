package com.self.common.api.req.token;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

}
