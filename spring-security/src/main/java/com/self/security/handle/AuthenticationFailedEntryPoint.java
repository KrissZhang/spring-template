package com.self.security.handle;

import com.alibaba.fastjson.JSON;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.RespCodeEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理
 */
@Component
public class AuthenticationFailedEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 3664356206581750369L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        String msg = "认证失败";

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(ResultEntity.addError(RespCodeEnum.FAIL_UNAUTHORIZED.getCode(), msg)));
    }

}
