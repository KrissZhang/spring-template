package com.self.security.api.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SmsLoginReq {

    @NotBlank(message = "手机号码不能为空")
    private String telPhoneNum;

    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

}
