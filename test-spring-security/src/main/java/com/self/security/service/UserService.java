package com.self.security.service;

import com.google.common.collect.Lists;
import com.self.security.bean.JWTInfo;
import com.self.security.entity.User;
import com.self.security.utils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户
     */
    public User selectUserByUserName(String userName){
        //查询用户名指定用户--TODO
        User user = new User();
        user.setId(1);
        user.setUserName(userName);
        user.setPassword(DigestUtils.md5Hex("123456"));
        user.setRealName("用户1");
        user.setDeleteFlag(1);

        return user;
    }

    /**
     * 根据用户id查询用户权限
     * @param userId 用户id
     * @return 用户权限
     */
    public List<String> selectPermissionListByUserId(Integer userId){
        //获取指定用户权限--TODO
        return Lists.newArrayList("sys_admin", "sys_user");
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
