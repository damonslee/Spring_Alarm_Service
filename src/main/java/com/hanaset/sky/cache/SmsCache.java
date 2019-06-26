package com.hanaset.sky.cache;

import com.hanaset.sky.entitiy.SkySmsTemplateEntity;
import com.hanaset.sky.repository.SkySmsTemplateRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Data
@Component
public class SmsCache {

    @Autowired
    private SkySmsTemplateRepository skySmsTemplateRepository;

    private List<SkySmsTemplateEntity> skySmsTemplateEntityList;

    public SkySmsTemplateEntity findForTemplate(String code){

        for(SkySmsTemplateEntity entity : skySmsTemplateEntityList){
            if(entity.getId().equals(code)){
                return entity;
            }
        }

        return null;
    }

    @PostConstruct
    private void update(){
        skySmsTemplateEntityList = skySmsTemplateRepository.findAll();
    }
}
