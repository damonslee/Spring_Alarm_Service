package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import com.hanaset.sky.repository.SkySmsTemplateRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Data
@Component
public class SmsCache {

    @Autowired
    SkySmsTemplateRepository skySmsTemplateRepository;

    HashMap<String, SkySmsTemplateEntity> gdacSkySmsTemplateEntityHashMap = new HashMap<>();

    public SkySmsTemplateEntity findForTemplate(String code){

        return gdacSkySmsTemplateEntityHashMap.get(code);
    }

    @Scheduled(fixedDelay = 60000)
    public void update(){
        List<SkySmsTemplateEntity> skySmsTemplateEntityList = skySmsTemplateRepository.findAll();

        gdacSkySmsTemplateEntityHashMap.clear();
        for(SkySmsTemplateEntity entity : skySmsTemplateEntityList){
            gdacSkySmsTemplateEntityHashMap.put(entity.getCode(), entity);
            //System.out.println(entity);
        }

        log.info("SMSCache update");
    }
}
