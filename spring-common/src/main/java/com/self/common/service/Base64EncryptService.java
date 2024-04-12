package com.self.common.service;

import com.self.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64加密方法
 */
public class Base64EncryptService implements EncryptService {

    private static final Logger logger = LoggerFactory.getLogger(Base64EncryptService.class);

    @Override
    public String encrypt(String content) {
        try {
            return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("加密失败：", e);
            throw new BizException("加密失败");
        }
    }

    @Override
    public String decrypt(String content) {
        try {
            byte[] asBytes = Base64.getDecoder().decode(content);
            return new String(asBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("解密失败：", e);
            throw new BizException("解密失败");
        }
    }

}
