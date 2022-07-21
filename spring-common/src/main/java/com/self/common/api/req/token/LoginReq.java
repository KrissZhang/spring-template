package com.self.common.api.req.token;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "用户名密码认证参数")
@Data
public class LoginReq {

    @Schema(name = "用户名", description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @Schema(name = "密码", description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

}
