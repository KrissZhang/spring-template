package com.self.security.service;

import com.self.security.api.req.LoginReq;
import com.self.security.api.req.SmsLoginReq;
import com.self.security.bean.AuthUser;
import com.self.security.constants.SecurityConstants;
import com.self.security.domain.ResultEntity;
import com.self.security.enums.TerminalTypeEnum;
import com.self.security.token.SmsAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
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
        String terminalType = request.getHeader(SecurityConstants.TERMINAL_TYPE);

        //调用 loadUserByXXX 方法
        Authentication authentication = authenticationManager.authenticate(abstractAuthenticationToken);
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        authUser.setTerminalType(StringUtils.isBlank(terminalType) ? TerminalTypeEnum.WEB.getValue() : StringUtils.lowerCase(terminalType));

        //生成token
        String token = jwtTokenService.createToken(authUser);
        authUser.setTokenId(token);

        return authUser;
    }

}
