package com.self.web.controller.system;

import com.self.common.api.req.token.LoginReq;
import com.self.common.api.req.token.SmsLoginReq;
import com.self.security.bean.AuthUser;
import com.self.common.constants.ApiURI;
import com.self.common.domain.ResultEntity;
import com.self.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping(value = ApiURI.TOKEN_LOGIN)
    public ResultEntity<AuthUser> login(HttpServletRequest request, @RequestBody @Validated LoginReq loginReq){
        return tokenService.login(request, loginReq);
    }

    @PostMapping(value = ApiURI.TOKEN_SMS_LOGIN)
    public ResultEntity<AuthUser> smsLogin(HttpServletRequest request, @RequestBody @Validated SmsLoginReq smsLoginReq){
        return tokenService.smsLogin(request, smsLoginReq);
    }

}
