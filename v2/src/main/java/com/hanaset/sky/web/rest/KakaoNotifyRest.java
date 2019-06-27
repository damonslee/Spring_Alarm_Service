package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.KakaoNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/kakao")
public class KakaoNotifyRest {

    @Autowired
    private KakaoNotifyService kakaoNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseItem> requestKakao(@RequestBody @Valid RequestMsg request) {

        return kakaoNotifyService.sendMessage(request);
    }
}
