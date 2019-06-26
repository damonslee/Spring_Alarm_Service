package com.hanaset.sky.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EmailConfig {

    private String accessKeyId = "";

    private String secretAccessKey = "";

    private String region = "";

    private String from = "";
    // currently, sending EMAIL via SES is not supported in AWS Seoul region

    private String host = "";

    private int port = 587;
}
