package com.self.web.controller.system;

import com.self.common.api.req.token.LoginReq;
import com.self.common.api.req.token.SmsLoginReq;
import com.self.security.bean.AuthUser;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.security.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户认证")
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "用户名密码认证")
    @PostMapping(value = ApiURI.TOKEN_LOGIN)
    public ResultEntity<AuthUser> login(HttpServletRequest request, @RequestBody @Validated LoginReq loginReq){
        return tokenService.login(request, loginReq);
    }

    @Operation(summary = "手机短信认证")
    @PostMapping(value = ApiURI.TOKEN_SMS_LOGIN)
    public ResultEntity<AuthUser> smsLogin(HttpServletRequest request, @RequestBody @Validated SmsLoginReq smsLoginReq){
        return tokenService.smsLogin(request, smsLoginReq);
    }

}
