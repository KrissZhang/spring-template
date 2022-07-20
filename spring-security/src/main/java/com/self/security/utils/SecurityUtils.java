package com.self.security.utils;

import com.self.security.bean.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    public static AuthUser getAuthUser(){
        try{
            return (AuthUser) getAuthentication().getPrincipal();
        }catch (Exception e){
            throw new RuntimeException("获取认证用户失败：", e);
        }
    }

    public static Integer getUserId(){
        return getAuthUser().getUserId();
    }

    public static String getUserName(){
        return getAuthUser().getUsername();
    }

    public static String encryptPassword(String rawPassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    public static boolean matchesPassword(String rawPassword, String encodePassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(rawPassword, encodePassword);
    }

    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
