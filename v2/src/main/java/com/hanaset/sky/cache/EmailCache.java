package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkyEmailTemplateEntity;
import com.hanaset.sky.repository.SkyEmailTemplateRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@Data
public class EmailCache {

    @Autowired
    SkyEmailTemplateRepository skyEmailTemplateRepository;

    HashMap<String, SkyEmailTemplateEntity> gdacSkyEmailTemplateEntityHashMap = new HashMap<>();

    public SkyEmailTemplateEntity findForTemplate(String code){

        return gdacSkyEmailTemplateEntityHashMap.get(code);
    }

    @Scheduled(fixedDelay = 60000)
    public void update(){
        List<SkyEmailTemplateEntity> skyEmailTemplateEntityList = skyEmailTemplateRepository.findAll();

        gdacSkyEmailTemplateEntityHashMap.clear();
        for(SkyEmailTemplateEntity entity : skyEmailTemplateEntityList){
            gdacSkyEmailTemplateEntityHashMap.put(entity.getCode(), entity);
            //System.out.println(entity);
        }

        log.info("EmailCache Update");
    }
}
