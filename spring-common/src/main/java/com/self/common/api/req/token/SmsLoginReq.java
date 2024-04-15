package com.self.common.api.req.token;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "手机短信认证参数")
@Data
public class SmsLoginReq {

    @Schema(name = "手机号码", description = "手机号码", required = true)
    @NotBlank(message = "手机号码不能为空")
    private String telPhoneNum;

    @Schema(name = "验证码", description = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String validateCode;

}
