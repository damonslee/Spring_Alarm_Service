package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.SmsNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/sms")
public class SmsNotifyRest {

    @Autowired
    SmsNotifyService smsNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseItem> requestSMS(@RequestBody @Valid RequestMsg request) {

        return smsNotifyService.sendMessage(request);
    }

}
