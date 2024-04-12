package com.self.common.service;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.self.common.exception.BizException;
import com.self.common.properties.EncryptProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Aes加密方法
 */
public class AesEncryptService implements EncryptService {

    private static final Logger logger = LoggerFactory.getLogger(AesEncryptService.class);

    @Autowired
    private EncryptProperties encryptProperties;

    @Override
    public String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(encryptProperties.getAesMode());
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptProperties.getAesKey().getBytes(StandardCharsets.UTF_8), Constants.AES));
            byte[] doFinal = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(doFinal);
        } catch (Exception e) {
            logger.error("加密失败：", e);
            throw new BizException("加密失败");
        }
    }

    @Override
    public String decrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(encryptProperties.getAesMode());
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptProperties.getAesKey().getBytes(StandardCharsets.UTF_8), Constants.AES));
            byte[] doFinal = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(doFinal);
        } catch (Exception e) {
            logger.error("解密失败：", e);
            throw new BizException("解密失败");
        }
    }

}
