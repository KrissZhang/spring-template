package com.self.security.service;

import com.self.security.bean.AuthUser;
import com.self.security.bean.JWTInfo;
import com.self.security.entity.User;
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
            throw new RuntimeException("认证用户不存在");
        }
        JWTInfo jwtInfo = userService.buildJWTInfoByUser(user);

        //获取权限列表
        List<String> permissionList = userService.selectPermissionListByUserId(user.getId());

        return new AuthUser(jwtInfo, permissionList);
    }

}
