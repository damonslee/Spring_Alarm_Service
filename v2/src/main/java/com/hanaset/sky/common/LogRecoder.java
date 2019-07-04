package com.hanaset.sky.common;

import com.hanaset.sky.entity.SkyMsgLogEntity;
import com.hanaset.sky.repository.SkyMsgLogRepository;
import com.hanaset.sky.requestmsg.RequestMsg;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class LogRecoder {

    @Autowired
    SkyMsgLogRepository logRepository;

    public void recordLog(RequestMsg request, String result, Timestamp reqTime) {

        if (request.getParam() == null) {
            request.setParam(new JSONObject());
        }

        SkyMsgLogEntity logEntity = SkyMsgLogEntity.builder()
                .code(request.getCode())
                .address(request.getTo())
                .msgType("kakao-sms")
                .param(request.getParam().toString())
                .reqTime(reqTime)
                .compTime(Timestamp.valueOf(LocalDateTime.now()))
                .result(result)
                .build();

        logRepository.save(logEntity);
    }
}
