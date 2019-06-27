package com.hanaset.sky.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "sqs")
public class SQSConfig {

    private String accessKey;
    private String secretKey;
    private String url;
    private String region;
    private String name;

}
