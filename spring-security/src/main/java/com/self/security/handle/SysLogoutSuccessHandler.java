package com.self.security.handle;

import com.alibaba.fastjson.JSON;
import com.self.common.enums.RespCodeEnum;
import com.self.security.bean.AuthUser;
import com.self.common.domain.ResultEntity;
import com.self.security.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 退出处理
 */
@Configuration
public class SysLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthUser authUser = jwtTokenService.getAuthUserByRequest(request);
        if(Objects.nonNull(authUser)){
            //删除缓存认证令牌
            jwtTokenService.removeToken(authUser.getTokenId());
        }

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(ResultEntity.addError(RespCodeEnum.SUCCESS.getCode(), "退出成功")));
    }

}
