package com.self.security.service;

import com.self.biz.exception.ParamException;
import com.self.common.utils.BeanUtils;
import com.self.dao.entity.User;
import com.self.security.bean.AuthUser;
import com.self.security.bean.JWTInfo;
import com.self.biz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 用户认证服务
 */
@Service
public class SysUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //获取认证用户
        User user = userService.selectUserByUserName(userName);
        if(Objects.isNull(user)){
            throw new ParamException("认证用户不存在");
        }
        JWTInfo jwtInfo = buildJWTInfoByUser(user);

        //获取权限列表
        List<String> permissionList = userService.selectPermissionListByUserId(user.getId());

        return new AuthUser(jwtInfo, permissionList);
    }

    public UserDetails loadUserByTelPhoneNum(String telPhoneNum) {
        //获取认证用户
        User user = userService.selectUserByTelPhoneNum(telPhoneNum);
        if(Objects.isNull(user)){
            throw new ParamException("认证用户不存在");
        }
        JWTInfo jwtInfo = buildJWTInfoByUser(user);

        //获取权限列表
        List<String> permissionList = userService.selectPermissionListByUserId(user.getId());

        return new AuthUser(jwtInfo, permissionList);
    }

    /**
     * 根据用户获取 JWTInfo
     * @param user 用户
     * @return JWTInfo
     */
    @SuppressWarnings({"all"})
    public JWTInfo buildJWTInfoByUser(User user){
        JWTInfo jwtInfo = BeanUtils.copyProperties(user, JWTInfo.class);
        jwtInfo.setUserId(user.getId());

        return jwtInfo;
    }

}
