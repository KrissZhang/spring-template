package com.self.security.service;

import com.self.security.api.req.LoginReq;
import com.self.security.bean.AuthUser;
import com.self.security.constants.SecurityConstants;
import com.self.security.domain.ResultEntity;
import com.self.security.enums.TerminalTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
        String terminalType = request.getHeader(SecurityConstants.TERMINAL_TYPE);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReq.getUserName(), loginReq.getPassword());

        //调用 loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        authUser.setTerminalType(StringUtils.isBlank(terminalType) ? TerminalTypeEnum.WEB.getValue() : StringUtils.lowerCase(terminalType));

        //生成token
        String token = jwtTokenService.createToken(authUser);
        authUser.setTokenId(token);

        return ResultEntity.ok(authUser);
    }

}
