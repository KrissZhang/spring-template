package com.self.common.config;

import com.self.common.constants.CfgConstants;
import net.anumbrella.seaweedfs.core.ConnectionProperties;
import net.anumbrella.seaweedfs.core.FileSource;
import net.anumbrella.seaweedfs.core.FileTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeaweedFSConfig {

    private static final Logger logger = LoggerFactory.getLogger(SeaweedFSConfig.class);

    @Value(CfgConstants.SEAWEEDFS_HOST)
    private String seaweedFSHost;

    @Value(CfgConstants.SEAWEEDFS_PORT)
    private Integer seaweedFSPort;

    @Value(CfgConstants.SEAWEEDFS_USE_PUBLIC)
    private Boolean seaweedFSUsePublic;

    @Bean
    public FileTemplate fileTemplate() {
        FileSource fileSource = new FileSource();
        ConnectionProperties properties
                = new ConnectionProperties.Builder()
                .host(seaweedFSHost)
                .port(seaweedFSPort)
                .maxConnection(100).build();
        fileSource.setProperties(properties);

        // 启动服务
        try {
            fileSource.startup();
        } catch (Exception e) {
            logger.error("SeaweedFS文件服务启动失败：", e);
        }

        FileTemplate fileTemplate = new FileTemplate(fileSource.getConnection());
        fileTemplate.setUsingPublicUrl(seaweedFSUsePublic);

        return fileTemplate;
    }

}
