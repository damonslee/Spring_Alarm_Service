package com.hanaset.sky.service;

import com.hanaset.sky.MsgPool.MsgPoolKakao;
import com.hanaset.sky.cache.KakaoCache;
import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import com.hanaset.sky.entitiy.SkyParamEntity;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

@Service
public class KakaoNotifyService {

    @Autowired
    private MsgPoolKakao msgPoolKakao;

    @Autowired
    private KakaoCache kakaoCache;

    private String getMessage(JSONObject jsonObject, SkyKakaoTemplateEntity skyKakaoTemplateEntity){

        String message = skyKakaoTemplateEntity.getContext();

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

    public String MsgPoolPush(List<JSONObject> request){

        for(JSONObject param : request) {

            try {
                SkyKakaoTemplateEntity skyKakaoTemplateEntity = kakaoCache.findForTemplate(param.get("code").toString());

                String result = vaildParam(skyKakaoTemplateEntity, param);

                if(!result.equals("success")){
                    System.out.println(result);
                    return result;
                }

                String msg = getMessage(param, skyKakaoTemplateEntity);

                msgPoolKakao.push_back(msg, skyKakaoTemplateEntity, param.get("num").toString());

            } catch (NullPointerException e) {

                return "Null error";
            }
        }

        return "success";
    }

    private String vaildParam(SkyKakaoTemplateEntity entity, JSONObject object){

        SkyParamEntity paramEntity = entity.getParam();

        String params = paramEntity.getParams();
        params += ",code,num";

        if(params.isEmpty())
            return "success";

        String[] array_param = params.split(",");


        for(String param : array_param){

            try{
                String temp = object.get(param).toString();
            }catch (NullPointerException e){
                return "\"" + param + "\"" + "is invaild";
            }
        }

        return "success";
    }
}

