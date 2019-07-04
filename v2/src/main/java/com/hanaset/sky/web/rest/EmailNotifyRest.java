package com.hanaset.sky.web.rest;

import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.EmailNotifyService;
import com.hanaset.sky.web.rest.support.SkyApiRestSupport;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Email message Notify", value = "이메일 전송")
@RestController
@RequestMapping("/email")
public class EmailNotifyRest extends SkyApiRestSupport {

    @Autowired
    private EmailNotifyService emailNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity requestEmail(@RequestBody @Valid RequestMsg request) {

        emailNotifyService.sendMessage(request);

        return response(null);
    }

}
