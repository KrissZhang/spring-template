package com.self.security.service;

import com.google.common.collect.Maps;
import com.self.security.bean.AuthUser;
import com.self.security.constants.SecurityConstants;
import com.self.common.utils.RedisUtils;
import com.self.common.utils.UUIDUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * JwtToken服务
 */
@Component
public class JwtTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    /**
     * 令牌密钥
     */
    private static final String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 令牌过期时间(毫秒)
     */
    private static final long EXPIRE_TIME_MS = 8 * 60 * 60 * 1000L;

    /**
     * 令牌刷新剩余有效时间(毫秒)
     */
    private static final long LEFT_TIME_MS = 20 * 60 * 1000L;

    /**
     * 签发过期时间(毫秒)
     */
    private static final long ISSUED_EXPIRE_TIME_MS = 2 * 24 * 60 * 60 * 1000L;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 通过请求获取认证用户
     * @param request 认证请求
     * @return 认证用户
     */
    public AuthUser getAuthUserByRequest(HttpServletRequest request){
        try{
            String token = request.getHeader(SecurityConstants.ACCESS_TOKEN);
            if(StringUtils.isBlank(token)){
                return null;
            }

            Claims claims = getClaimsByToken(token);

            long currentTimeMillis = System.currentTimeMillis();
            Date issuedDate = claims.getIssuedAt();

            String tokenId = Optional.ofNullable(claims.get(SecurityConstants.TOKEN_ID)).orElse("").toString();
            String tokenKey = SecurityConstants.PREFIX_AUTH_USER_DETAIL + tokenId;

            //签发时间不能超过48小时
            if(currentTimeMillis - issuedDate.getTime() > ISSUED_EXPIRE_TIME_MS){
                redisUtils.del(tokenKey);
                return null;
            }

            AuthUser authUser = redisUtils.get(tokenKey);
            if(Objects.nonNull(authUser) && (authUser.getExpiresTimeMs() > currentTimeMillis)){
                return authUser;
            }
        }catch (Exception e){
            logger.error("获取认证用户失败：", e);
            return null;
        }

        return null;
    }

    /**
     * 创建令牌
     */
    public String createToken(AuthUser authUser){
        String tokenId = UUIDUtils.getShortUUID();
        authUser.setTokenId(tokenId);
        refreshToken(authUser);

        Map<String, Object> claims = Maps.newHashMap();
        claims.put(SecurityConstants.TOKEN_ID, tokenId);
        return createTokenByClaims(claims);
    }

    /**
     * 删除令牌
     */
    public void removeToken(String tokenId){
        if(StringUtils.isNotBlank(tokenId)){
            String tokenKey = SecurityConstants.PREFIX_AUTH_USER_DETAIL + tokenId;
            redisUtils.del(tokenKey);
        }
    }

    /**
     * 检查令牌剩余有效时间并刷新
     */
    public void checkAndRefreshToken(AuthUser authUser){
        long leftTimeMs = authUser.getExpiresTimeMs() - System.currentTimeMillis();
        //有效时间剩余20分钟刷新令牌
        if(leftTimeMs > 0 && leftTimeMs <= LEFT_TIME_MS){
            refreshToken(authUser);
        }
    }

    /**
     * 刷新令牌
     */
    private void refreshToken(AuthUser authUser){
        authUser.setAuthTimeMs(System.currentTimeMillis());
        authUser.setExpiresTimeMs(authUser.getAuthTimeMs() + EXPIRE_TIME_MS);

        String tokenKey = SecurityConstants.PREFIX_AUTH_USER_DETAIL + authUser.getTokenId();
        redisUtils.set(tokenKey, authUser, EXPIRE_TIME_MS);
    }

    /**
     * 根据数据声明生成令牌
     * @param claims 数据声明
     * @return 令牌
     */
    private String createTokenByClaims(Map<String, Object> claims){
        return Jwts.builder()
                //数据声明
                .setClaims(claims)

                //签发时间
                .setIssuedAt(new Date())

                //签名
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

    /**
     * 根据令牌获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsByToken(String token){
        return Jwts.parser()
                //签名密钥
                .setSigningKey(SECRET)

                //解析令牌
                .parseClaimsJws(token).getBody();
    }

}
