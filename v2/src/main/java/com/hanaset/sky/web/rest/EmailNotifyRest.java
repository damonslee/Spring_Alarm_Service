package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.requestmsg.RequestMsg;
import com.hanaset.sky.service.EmailNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/email")
public class EmailNotifyRest {

    @Autowired
    private EmailNotifyService emailNotifyService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseItem> requestEmail(@RequestBody @Valid RequestMsg request) {

        return emailNotifyService.sendMessage(request);
    }

}