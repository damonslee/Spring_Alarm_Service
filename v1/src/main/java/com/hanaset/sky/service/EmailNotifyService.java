package com.hanaset.sky.service;

import com.hanaset.sky.MsgPool.MsgPoolEmail;
import com.hanaset.sky.cache.EmailCache;
import com.hanaset.sky.entitiy.SkyEmailTemplateEntity;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;


@Service
public class EmailNotifyService {

    @Autowired
    MsgPoolEmail msgPoolEmail;

    @Autowired
    EmailCache emailCache;

    private String getMessage(JSONObject jsonObject, SkyEmailTemplateEntity entity){

        ClassPathResource cpr = new ClassPathResource("/templates/" + entity.getFileName());

        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            String message = new String(bytes, StandardCharsets.UTF_8);

            Set key = jsonObject.keySet();
            Iterator<String> iterator = key.iterator();

            while (iterator.hasNext()) {

                String keyName = iterator.next();
                String replaceKey = "__" + keyName + "__";

                try {
                    message = message.replaceAll(replaceKey, jsonObject.get(keyName).toString());
                } catch (PatternSyntaxException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(message);

            return message;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void MsgPoolPush(List<JSONObject> request){

        try {
            for (JSONObject param : request) {

                SkyEmailTemplateEntity entity = emailCache.findForTemplate(param.get("code").toString());

                String msg = getMessage(param, entity);

                msgPoolEmail.push_back(msg, entity, param.get("to").toString());

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
