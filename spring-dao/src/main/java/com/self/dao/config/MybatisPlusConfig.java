package com.self.dao.config;

import com.self.common.enums.AlgorithmEnum;
import com.self.common.properties.EncryptProperties;
import com.self.common.service.AesEncryptService;
import com.self.common.service.Base64EncryptService;
import com.self.common.service.EncryptService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus配置
 */
@Configuration
@MapperScan("com.self.dao.mapper")
@EnableConfigurationProperties({EncryptProperties.class})
public class MybatisPlusConfig {

    @Autowired
    private EncryptProperties encryptProperties;

    @Bean
    public EncryptTypeHandler encryptTypeHandler() {
        return new EncryptTypeHandler();
    }

    @Bean
    @ConditionalOnMissingBean(EncryptService.class)
    public EncryptService encryptService() {
        AlgorithmEnum algorithm = encryptProperties.getAlgorithm();
        EncryptService encryptService;
        switch (algorithm) {
            case BASE64:
                encryptService = new Base64EncryptService();
                break;
            case AES:
                encryptService = new AesEncryptService();
                break;
            default:
                encryptService = null;
        }

        return encryptService;
    }

}
