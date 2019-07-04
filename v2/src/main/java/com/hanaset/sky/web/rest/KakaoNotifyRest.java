package com.hanaset.sky.web.rest;

import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.KakaoNotifyService;
import com.hanaset.sky.web.rest.support.SkyApiRestSupport;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "KAKAO message Notify", value = "카카오톡 메세지 전송")
@RestController
@RequestMapping("/kakao")
public class KakaoNotifyRest extends SkyApiRestSupport {

    @Autowired
    private KakaoNotifyService kakaoNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity requestKakao(@RequestBody @Valid RequestMsg request) {

        kakaoNotifyService.sendMessage(request);

        return response(null);
    }
}
