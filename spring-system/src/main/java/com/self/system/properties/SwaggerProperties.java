package com.self.system.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.swagger")
@Data
public class SwaggerProperties {

    private Boolean enable;

    private String groupName;

    private String basePackage;

    private String version;

    private String title;

    private String description;

    private String contactName;

    private String contactEmail;

    private String contactUrl;

}
