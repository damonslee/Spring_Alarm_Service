package com.hanaset.sky.service;

import com.hanaset.sky.MsgPool.MsgPoolSms;
import com.hanaset.sky.cache.SmsCache;
import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

@Service
public class SmsNotifyService {

    @Autowired
    SmsCache smsCache;

    @Autowired
    MsgPoolSms msgPoolSms;

    private String getMessage(JSONObject jsonObject, SkySmsTemplateEntity skySmsTemplateEntity){

        String message = skySmsTemplateEntity.getTemplate();

        Set key = jsonObject.keySet();
        Iterator<String> iterator = key.iterator();

        while (iterator.hasNext()){

            String keyName = iterator.next();
            String replaceKey = "#\\{" + keyName + "\\}";

            try {
                message = message.replaceAll(replaceKey, jsonObject.get(keyName).toString());
            }catch (PatternSyntaxException e)
            {
                e.printStackTrace();
            }
        }

        System.out.println(message);

        return message;
    }

    public void MsgPoolPush(List<JSONObject> request){

        try {
            for (JSONObject param : request) {

                SkySmsTemplateEntity entity = smsCache.findForTemplate(param.get("code").toString());

                String msg = getMessage(param, entity);

                msgPoolSms.push_back(param.get("num").toString(), entity, msg);

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
