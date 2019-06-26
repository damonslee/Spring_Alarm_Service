package com.hanaset.sky.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KakaoConfig{

    @Getter
    static String id = "";

    @Getter
    final static String host = "";

    @Getter
    final static String path = "";

    @Getter
    final static String key = "";
}


