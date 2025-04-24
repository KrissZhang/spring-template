package com.self.common.config;

import com.self.common.constants.CfgConstants;
import com.self.common.constants.CommonConstants;
import io.minio.MinioClient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(MinioClientConfig.class);

    @Value(CfgConstants.MINIO_URL)
    private String minioUrl;

    @Value(CfgConstants.MINIO_PORT)
    private String minioPort;

    @Value(CfgConstants.MINIO_ACCESS_KEY)
    private String minioAccessKey;

    @Value(CfgConstants.MINIO_SECRET_KEY)
    private String minioSecretKey;

    @Value(CfgConstants.MINIO_BUCKET_NAME)
    private String minioBucketName;

    @Value(CfgConstants.MINIO_SECURE)
    private Boolean minioSecure;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint((minioUrl + CommonConstants.STR_COLON + minioPort)).credentials(minioAccessKey, minioSecretKey).build();
    }

}
