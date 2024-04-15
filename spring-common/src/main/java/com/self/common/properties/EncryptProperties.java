package com.self.common.properties;

import com.self.common.enums.AlgorithmEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mybatis-plus.encrypt")
@Data
public class EncryptProperties {

    /**
     * 加密算法
     */
    private AlgorithmEnum algorithm = AlgorithmEnum.BASE64;

    /**
     * AES加密模式：Ecb
     */
    private String aesMode = "AES/ECB/PKCS5Padding";

    /**
     * AES密钥Key
     */
    private String aesKey = "";

}
