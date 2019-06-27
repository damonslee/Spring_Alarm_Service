package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkyKakaoTemplateEntity;
import com.hanaset.sky.repository.SkyKakaoTemplateRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Data
public class KakaoCache {

    @Autowired
    SkyKakaoTemplateRepository skyKakaoTemplateRepository;

    List<SkyKakaoTemplateEntity> skyKakaoTemplateEntityList;


    public SkyKakaoTemplateEntity findForTemplate (String code){

        for(SkyKakaoTemplateEntity skyKakaoTemplateEntity : skyKakaoTemplateEntityList){

            if(skyKakaoTemplateEntity.getId().equals(code)){
                return skyKakaoTemplateEntity;
            }
        }

        return null;
    }

    @PostConstruct
    public void update(){
        skyKakaoTemplateEntityList = skyKakaoTemplateRepository.findAll();
    }
}
