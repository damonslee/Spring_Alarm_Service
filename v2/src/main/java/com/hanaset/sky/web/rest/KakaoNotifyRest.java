package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.KakaoNotifyService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "KAKAO message Notify", value = "카카오톡 메세지 전송")
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
