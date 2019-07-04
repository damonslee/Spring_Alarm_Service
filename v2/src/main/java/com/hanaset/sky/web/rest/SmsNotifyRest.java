package com.hanaset.sky.web.rest;

import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.SmsNotifyService;
import com.hanaset.sky.web.rest.support.SkyApiRestSupport;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "SMS message Notify", value = "문자 메세지 전송")
@RestController
@RequestMapping("/sms")
public class SmsNotifyRest extends SkyApiRestSupport {

    @Autowired
    SmsNotifyService smsNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity requestSMS(@RequestBody @Valid RequestMsg request) {

        smsNotifyService.sendMessage(request);

        return response(null);
    }

}
