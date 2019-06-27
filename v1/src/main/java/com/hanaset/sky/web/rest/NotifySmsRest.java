package com.hanaset.sky.web.rest;

import com.hanaset.sky.service.SmsNotifyService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sms")
public class NotifySmsRest implements NotifyRestImp{

    @Autowired
    SmsNotifyService smsNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public String requestSend(@RequestBody List<JSONObject> request){

        smsNotifyService.MsgPoolPush(request);
        return "success";
    }

}
