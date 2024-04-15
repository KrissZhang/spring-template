package com.self.security.constants;

/**
 * 安全服务常量
 */
public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String ACCESS_TOKEN = "access_token";

    public static final String TERMINAL_TYPE = "terminalType";

    public static final String TOKEN_ID = "tokenId";

    public static final String PREFIX_AUTH_USER_DETAIL = "auth:user:token:detail:";

    /**
     * 验证码key固定盐
     */
    public static final String SALT_VALIDATE_KEY = "auth:validate:key:salt:";

    /**
     * 登陆验证码 key
     */
    public static final String PREFIX_AUTH_VALIDATE_KEY = "auth:validate:key:";

}
