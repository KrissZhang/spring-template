package com.self.common.utils;

import com.self.common.context.JWTInfoContext;
import com.self.common.exception.UnAuthorizedException;
import com.self.common.jwt.JWTInfo;

public class CurUserUtils {

    private CurUserUtils(){
    }

    public static JWTInfo curJWTInfo(){
        JWTInfo jwtInfo = JWTInfoContext.get();
        if(jwtInfo == null){
            throw new UnAuthorizedException("获取当前用户异常");
        }

        return jwtInfo;
    }

}
