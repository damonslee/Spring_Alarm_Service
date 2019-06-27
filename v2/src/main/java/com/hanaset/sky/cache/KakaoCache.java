package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import com.hanaset.sky.repository.SkyKakaoTemplateRepository;
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
public class KakaoCache {

    @Autowired
    SkyKakaoTemplateRepository skyKakaoTemplateRepository;

    HashMap<String, SkyKakaoTemplateEntity> gdacSkyKakaoTemplateEntityHashMap = new HashMap<>();


    public SkyKakaoTemplateEntity findForTemplate (String code){

        return gdacSkyKakaoTemplateEntityHashMap.get(code);
    }

    @Scheduled(fixedDelay = 60000)
    public void update(){
        List<SkyKakaoTemplateEntity> skyKakaoTemplateEntityList = skyKakaoTemplateRepository.findAll();

        gdacSkyKakaoTemplateEntityHashMap.clear();
        for(SkyKakaoTemplateEntity entity : skyKakaoTemplateEntityList){
            gdacSkyKakaoTemplateEntityHashMap.put(entity.getCode(), entity);
            //System.out.println(entity);
        }

        log.info("KakaoCache Update");
    }
}
