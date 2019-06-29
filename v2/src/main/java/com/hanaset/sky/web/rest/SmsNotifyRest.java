package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.SmsNotifyService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@Api(tags = "SMS message Notify", value = "문자 메세지 전송")
@RestController
@RequestMapping("/sms")
public class SmsNotifyRest {

    @Autowired
    SmsNotifyService smsNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseItem> requestSMS(@RequestBody @Valid RequestMsg request) {

        return smsNotifyService.sendMessage(request);
    }

    @PostMapping(value = "/test", consumes = "application/json", produces = "application/json")
    public String requestTest(@RequestBody @Valid HashMap<String, Object> request) {

        System.out.println(request.size());
        System.out.println(request.toString());
        return request.toString();
    }

}
