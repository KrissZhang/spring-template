package com.self.security.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.self.common.api.req.token.LoginReq;
import com.self.common.api.req.token.SmsLoginReq;
import com.self.common.domain.ResultEntity;
import com.self.common.enums.RespCodeEnum;
import com.self.common.utils.RedisUtils;
import com.self.common.utils.ServletUtils;
import com.self.security.bean.AuthUser;
import com.self.security.constants.SecurityConstants;
import com.self.security.token.SmsAuthenticationToken;
import com.self.security.utils.SecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private RedisUtils redisUtils;

    public ResultEntity<String> validateKey(){
        try{
            String validateKey = DigestUtils.md5Hex(SecurityConstants.SALT_VALIDATE_KEY + System.currentTimeMillis());
            redisUtils.set(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + validateKey, "", 300);

            return ResultEntity.ok(validateKey);
        }catch (Exception e){
            logger.error("验证码key生成失败: ", e);
            return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), "验证码key生成失败");
        }
    }

    public void validateCode(HttpServletResponse response, String validateKey){
        try{
            if(StringUtils.isNotBlank(redisUtils.get(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + validateKey))){
                try{
                    redisUtils.del(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + validateKey);
                }catch (Exception e){
                    logger.error("删除登陆验证码key缓存失败: {}", validateKey, e);
                }
            }else{
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType("image/jpeg");

                //随机生成验证码字符
                RandomGenerator generator = new RandomGenerator("0123456789", 4);

                //返回验证码图片
                LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
                lineCaptcha.setGenerator(generator);
                lineCaptcha.createCode();

                redisUtils.set(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + validateKey, lineCaptcha.getCode(), 300);

                lineCaptcha.write(response.getOutputStream());
            }
        }catch (Exception e){
            logger.error("获取登陆验证码失败: ", e);
        }
    }

    public ResultEntity<AuthUser> login(HttpServletRequest request, LoginReq loginReq){
        //认证验证码信息
        String validateCode = redisUtils.get(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + loginReq.getValidateKey());
        if(StringUtils.isBlank(validateCode) || (!loginReq.getValidateCode().equalsIgnoreCase(validateCode))){
            return ResultEntity.addError(RespCodeEnum.FAIL_SYS.getCode(), "验证码错误");
        }else{
            redisUtils.del(SecurityConstants.PREFIX_AUTH_VALIDATE_KEY + loginReq.getValidateKey());
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReq.getUserName(), loginReq.getPassword());

        return ResultEntity.ok(doLogin(request, authenticationToken));
    }

    public ResultEntity<AuthUser> smsLogin(HttpServletRequest request, SmsLoginReq smsLoginReq){
        //认证验证码信息--TODO

        AbstractAuthenticationToken abstractAuthenticationToken = new SmsAuthenticationToken(smsLoginReq.getTelPhoneNum());

        return ResultEntity.ok(doLogin(request, abstractAuthenticationToken));
    }

    public ResultEntity<Object> logout(){
        AuthUser authUser = SecurityUtils.getAuthUser();
        if(Objects.isNull(authUser)){
            return ResultEntity.ok();
        }

        jwtTokenService.removeToken(authUser.getTokenId());

        return ResultEntity.ok();
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
