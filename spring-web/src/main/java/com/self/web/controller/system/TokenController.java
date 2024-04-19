package com.self.web.controller.system;

import com.self.common.annotation.OperLog;
import com.self.common.api.req.token.LoginReq;
import com.self.common.api.req.token.SmsLoginReq;
import com.self.common.enums.BusinessTypeEnum;
import com.self.security.bean.AuthUser;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.security.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "用户认证")
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "获取登陆验证码key")
    @GetMapping(value = ApiURI.TOKEN_VALIDATE_KEY)
    public ResultEntity<String> validateKey(){
        return tokenService.validateKey();
    }

    @Operation(summary = "获取登陆验证码")
    @GetMapping(value = ApiURI.TOKEN_VALIDATE_CODE)
    public void validateCode(HttpServletResponse response, @RequestParam String validateKey){
        tokenService.validateCode(response, validateKey);
    }

    @Operation(summary = "用户名密码认证")
    @OperLog(title = "用户名密码认证", businessType = BusinessTypeEnum.AUTH)
    @PostMapping(value = ApiURI.TOKEN_LOGIN)
    public ResultEntity<AuthUser> login(HttpServletRequest request, @RequestBody @Validated LoginReq loginReq){
        return tokenService.login(request, loginReq);
    }

    @Operation(summary = "手机短信认证")
    @OperLog(title = "手机短信认证", businessType = BusinessTypeEnum.AUTH)
    @PostMapping(value = ApiURI.TOKEN_SMS_LOGIN)
    public ResultEntity<AuthUser> smsLogin(HttpServletRequest request, @RequestBody @Validated SmsLoginReq smsLoginReq){
        return tokenService.smsLogin(request, smsLoginReq);
    }

    @Operation(summary = "退出认证")
    @OperLog(title = "退出认证", businessType = BusinessTypeEnum.AUTH)
    @PostMapping(value = ApiURI.TOKEN_LOGOUT)
    public ResultEntity<Object> logout(){
        return tokenService.logout();
    }

}
