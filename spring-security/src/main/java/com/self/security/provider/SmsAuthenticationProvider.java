package com.self.security.provider;

import com.self.security.service.SysUserDetailsService;
import com.self.security.token.SmsAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 验证码认证器
 */
@Component
public class SmsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SysUserDetailsService sysUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String telPhoneNum = Optional.ofNullable(authentication.getPrincipal()).orElse("").toString();
        UserDetails userDetails = sysUserDetailsService.loadUserByTelPhoneNum(telPhoneNum);

        SmsAuthenticationToken smsAuthenticationToken = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());
        smsAuthenticationToken.setDetails(authentication.getDetails());

        return smsAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SmsAuthenticationToken.class);
    }

}
