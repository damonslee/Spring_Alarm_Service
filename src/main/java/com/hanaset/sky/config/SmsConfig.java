package com.hanaset.sky.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SmsConfig {

    private String accessKeyId ="";

    private String secretAccessKey = "";

    private String region = "";
}
