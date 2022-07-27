package com.self.security.service;

import com.self.common.api.req.token.LoginReq;
import com.self.common.api.req.token.SmsLoginReq;
import com.self.common.domain.ResultEntity;
import com.self.common.utils.ServletUtils;
import com.self.security.bean.AuthUser;
import com.self.security.token.SmsAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class TokenService {

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    public ResultEntity<AuthUser> login(HttpServletRequest request, LoginReq loginReq){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReq.getUserName(), loginReq.getPassword());

        return ResultEntity.ok(doLogin(request, authenticationToken));
    }

    public ResultEntity<AuthUser> smsLogin(HttpServletRequest request, SmsLoginReq smsLoginReq){
        //认证验证码信息--TODO

        AbstractAuthenticationToken abstractAuthenticationToken = new SmsAuthenticationToken(smsLoginReq.getTelPhoneNum());

        return ResultEntity.ok(doLogin(request, abstractAuthenticationToken));
    }

    private AuthUser doLogin(HttpServletRequest request, AbstractAuthenticationToken abstractAuthenticationToken){
        //调用 loadUserByXXX 方法
        Authentication authentication = authenticationManager.authenticate(abstractAuthenticationToken);
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        authUser.setTerminalType(ServletUtils.getTerminalType());

        //生成token
        String token = jwtTokenService.createToken(authUser);
        authUser.setTokenId(token);

        return authUser;
    }

}
