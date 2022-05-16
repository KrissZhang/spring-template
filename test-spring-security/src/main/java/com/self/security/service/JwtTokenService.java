package com.self.security.service;

import com.self.security.bean.AuthUser;
import com.self.security.constants.SecurityConstants;
import com.self.security.utils.RedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
     * 令牌过期时间(秒)
     */
    private static final int EXPIRE_TIME = 3600;

    /**
     * 签发过期时间(秒)
     */
    private static final int ISSUED_EXPIRE_TIME = 86400;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 通过请求获取认证用户
     * @param request 认证请求
     * @return 认证用户
     */
    public AuthUser getAuthUserByRequest(HttpServletRequest request){
        AuthUser authUser = null;
        try{
            String token = request.getHeader(SecurityConstants.ACCESS_TOKEN);
            Claims claims = getClaimsByToken(token);

            long currentTimeMillis = System.currentTimeMillis();
            Date issuedDate = claims.getIssuedAt();

            String tokenId = Optional.ofNullable(claims.get("tokenId")).orElse("").toString();
            String tokenKey = SecurityConstants.PREFIX_AUTH_USER_DETAIL + tokenId;

            //签发时间不能超过24小时
            if(currentTimeMillis - issuedDate.getTime() > ISSUED_EXPIRE_TIME * 1000){
                redisUtils.del(tokenKey);
                return null;
            }

            authUser = redisUtils.get(tokenKey);
        }catch (Exception e){
            logger.error("获取认证用户失败：", e);
            return authUser;
        }

        return authUser;
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
