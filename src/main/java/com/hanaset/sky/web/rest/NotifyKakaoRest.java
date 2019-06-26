package com.hanaset.sky.web.rest;

import com.hanaset.sky.service.KakaoNotifyService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kakao")
public class NotifyKakaoRest implements NotifyRestImp{

    @Autowired
    private KakaoNotifyService kakaoNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public String requestSend(@RequestBody List<JSONObject> request){

        return kakaoNotifyService.MsgPoolPush(request);
    }
}
