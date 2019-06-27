package com.hanaset.sky.web.rest;

import com.hanaset.sky.service.EmailNotifyService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/email")
public class NotifyEmailRest implements NotifyRestImp{

    @Autowired
    private EmailNotifyService emailNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public String requestSend(@RequestBody List<JSONObject> request){

        emailNotifyService.MsgPoolPush(request);
        return "success";
    }
}
