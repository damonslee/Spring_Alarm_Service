package com.hanaset.sky.web.rest;

import com.hanaset.sky.item.ResponseItem;
import com.hanaset.sky.service.CodeOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CodeOfferRest {

    @Autowired
    CodeOfferService codeOfferService;

    @GetMapping("/list")
    public ResponseEntity<ResponseItem> requestList(String code) {
        return codeOfferService.getCodeList(code);
    }

    @GetMapping("/template")
    public ResponseEntity<ResponseItem> requestCode(String code, String template){
        return codeOfferService.getTemplate(code, template);
    }
}
