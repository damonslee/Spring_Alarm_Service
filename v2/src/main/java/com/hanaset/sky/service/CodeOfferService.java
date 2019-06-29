package com.hanaset.sky.service;

import com.hanaset.sky.cache.EmailCache;
import com.hanaset.sky.cache.KakaoCache;
import com.hanaset.sky.cache.SmsCache;
import com.hanaset.sky.entity.SkyEmailTemplateEntity;
import com.hanaset.sky.entity.SkyKakaoTemplateEntity;
import com.hanaset.sky.entity.SkySmsTemplateEntity;
import com.hanaset.sky.item.ResponseItem;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CodeOfferService {

    @Autowired
    KakaoCache kakaoCache;

    @Autowired
    SmsCache smsCache;

    @Autowired
    EmailCache emailCache;

    public ResponseEntity<ResponseItem> getCodeList(String code){

        ResponseItem responseItem;
        JSONObject kakaoObject, smsObject, emailObject, object;

        object = new JSONObject();

        if(code.equals("all") || code.equals("kakao")) {

            kakaoObject = new JSONObject();
            for (SkyKakaoTemplateEntity entity : kakaoCache.getSkyKakaoTemplateRepository().findAll()) {
                kakaoObject.put(entity.getId(), entity.getCode());
            }
            object.put("kakao", kakaoObject);

        }

        if(code.equals("all") || code.equals("sms")) {

            smsObject = new JSONObject();
            for (SkySmsTemplateEntity entity : smsCache.getSkySmsTemplateRepository().findAll()) {
                smsObject.put(entity.getId(), entity.getCode());
            }
            object.put("sms", smsObject);

        }

        if(code.equals("all") || code.equals("email")) {

            emailObject = new JSONObject();
            for (SkyEmailTemplateEntity entity : emailCache.getSkyEmailTemplateRepository().findAll()) {
                emailObject.put(entity.getId(), entity.getCode());
            }
            object.put("email", emailObject);

        }

        if(object.size() == 0) {
            object.put("result", "code is not kakao / email / sms");
        }

        responseItem = ResponseItem.builder()
                .code("0")
                .data(object)
                .build();

        return new ResponseEntity<ResponseItem>(responseItem, HttpStatus.OK);
    }

    public ResponseEntity<ResponseItem> getTemplate(String code, String template){

        JSONObject param = new JSONObject();

        if(code.equals("kakao")){
            param.put("param",kakaoCache.findForTemplate(template).getParam().getParams());
        }else if(code.equals("sms")){
            param.put("param",smsCache.findForTemplate(template).getParam().getParams());
        }else if(code.equals("email")){
            param.put("param",emailCache.findForTemplate(template).getParam().getParams());
        }else{
            param.put("error","code is not kakao / sms / email");
        }

        ResponseItem responseItem = ResponseItem.builder()
                .code("0")
                .data(param)
                .build();

        return new ResponseEntity<ResponseItem>(responseItem, HttpStatus.OK);
    }

}
