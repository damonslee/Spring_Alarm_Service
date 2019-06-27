package com.hanaset.sky.config;

import com.hanaset.sky.sender.EmailSender;
import com.hanaset.sky.sender.KakaoSender;
import com.hanaset.sky.sender.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfig implements ApplicationRunner {

    @Autowired
    KakaoSender kakaoSender;

    @Autowired
    EmailSender emailSender;

    @Autowired
    SmsSender smsSender;

    @Override
    public void run(ApplicationArguments args){
        kakaoSender.kakaoSend();
        emailSender.emailSend();
        smsSender.smsSend();
    }
}
